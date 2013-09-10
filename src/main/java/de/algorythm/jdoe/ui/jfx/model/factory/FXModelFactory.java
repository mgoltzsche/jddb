package de.algorythm.jdoe.ui.jfx.model.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javafx.application.Platform;
import de.algorythm.jdoe.cache.IObjectCache;
import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.dao.IModelFactory;
import de.algorythm.jdoe.model.dao.IObserver;
import de.algorythm.jdoe.model.dao.IPropertyValueLoader;
import de.algorythm.jdoe.model.dao.ModelChange;
import de.algorythm.jdoe.model.entity.IPropertyValueFactory;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.IFXPropertyValueChangeHandler;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociation;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociations;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAttribute;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;

public class FXModelFactory implements IModelFactory<FXEntity, IFXPropertyValue<?>, FXEntityReference>, IPropertyValueFactory<IFXPropertyValue<?>, FXEntityReference>, IObserver<FXEntity, IFXPropertyValue<?>, FXEntityReference> {

	private IObjectCache<FXEntity> entityCache;
	
	public void init(final IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao, final IObjectCache<FXEntity> entityCache) {
		this.entityCache = entityCache;
		
		dao.addObserver(this);
	}
	
	@Override
	public FXEntity createNewEntity(final EntityType type) {
		final FXEntity entity = new FXEntity(type);
		final Collection<Property> properties = type.getProperties();
		final ArrayList<IFXPropertyValue<?>> values = new ArrayList<>(properties.size());
		
		for (Property property : properties)
			values.add(property.createPropertyValue(this));
		
		entity.setValues(values);
		
		entityCache.put(entity.getId(), entity);
		
		return entity;
	}

	@Override
	public FXEntity createEntity(final IPropertyValueLoader<FXEntityReference> loader) {
		return createEntity(loader, true);
	}

	@Override
	public FXEntity createEntityReference(final IPropertyValueLoader<FXEntityReference> loader) {
		return createEntity(loader, false);
	}

	private FXEntity createEntity(final IPropertyValueLoader<FXEntityReference> loader, final boolean withAssociations) {
		final String id = loader.getId();
		final EntityType type = loader.getType();
		final Collection<Property> properties = type.getProperties();
		final FXEntity cachedEntity;
		FXEntity entity = null;
		final boolean instantiateEntity;
		
		synchronized(entityCache) {
			cachedEntity = entityCache.get(id);
			
			instantiateEntity = cachedEntity == null;
			
			if (instantiateEntity) {
				entity = new FXEntity(id, type, !withAssociations);
				entityCache.put(id, entity);
			}
		}
		
		if (instantiateEntity) {
			final ArrayList<IFXPropertyValue<?>> values = new ArrayList<>(properties.size());
			
			for (Property property : properties) {
				final IFXPropertyValue<?> propertyValue = property.createPropertyValue(this);
				
				if (withAssociations || !property.getType().isUserDefined())
					loader.load(propertyValue);
				
				values.add(propertyValue);
			}
			
			entity.setValues(values);
			
			if (withAssociations)
				setReferringEntitiesLater(entity, loader);
			
			entity.bindValues();
			
			return entity;
		} else if (withAssociations && cachedEntity.isReference()) {
			for (IFXPropertyValue<?> propertyValue : cachedEntity.getValues()) {
				propertyValue.setChangeHandler(IFXPropertyValueChangeHandler.DEFAULT);
				loader.load(propertyValue);
			}

			setReferringEntitiesLater(cachedEntity, loader);
			
			cachedEntity.setReference(false);
			cachedEntity.bindValues();
		}
		
		return cachedEntity;
	}
	
	private void setReferringEntitiesLater(final FXEntity entity, final IPropertyValueLoader<FXEntityReference> loader) {
		final Collection<FXEntityReference> referringEntities = loader.loadReferringEntities();
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				entity.setReferringEntities(referringEntities);
			}
		});
	}
	
	@Override
	public <V extends Comparable<V>> IFXPropertyValue<?> createAttributeValue(final Property property,
			AbstractAttributeType<V> type) {
		return new FXAttribute<V>(property, type);
	}

	@Override
	public IFXPropertyValue<?> createAssociationValue(final Property property) {
		return new FXAssociation(property);
	}

	@Override
	public IFXPropertyValue<?> createAssociationsValue(final Property property) {
		return new FXAssociations(property);
	}
	
	@Override
	public void update(final ModelChange<FXEntity,IFXPropertyValue<? extends Object>,FXEntityReference> change) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// remove references to deleted entity
				for (FXEntity entity : change.getDeleted()) {
					AssociationRemovingVisitor.removeAssociationsTo(entity, entityCache);
					
					for (FXEntityReference entityRef : AssociationCollectingVisitor.associations(entity)) {
						final FXEntity cachedEntity = entityCache.get(entityRef.getId());
						
						cachedEntity.getReferringEntities().remove(entity);
					}
				}
				
				// update saved cached entities
				for (FXEntity entity : change.getSaved().values()) {
					final FXEntity cachedEntity = entityCache.get(entity.getId());
					
					if (cachedEntity != null) {
						final Set<FXEntityReference> oldReferredEntities = ReferredEntityCollectingVisitor.referredEntities(cachedEntity);
						final Set<FXEntityReference> newReferredEntities = ReferredEntityCollectingVisitor.referredEntities(entity);
						final Set<FXEntityReference> tmpOldReferredEntities = new HashSet<>(oldReferredEntities);
						
						oldReferredEntities.removeAll(newReferredEntities);
						newReferredEntities.removeAll(tmpOldReferredEntities);
						
						// update referring entities
						for (FXEntityReference oldRef : oldReferredEntities) {
							final FXEntity cachedOldReferredEntity = entityCache.get(oldRef.getId());
							
							if (cachedOldReferredEntity != null && !cachedOldReferredEntity.isReference())
								cachedOldReferredEntity.getReferringEntities().remove(cachedEntity);
						}
						
						for (FXEntityReference newRef : newReferredEntities) {
							final FXEntity cachedNewReferredEntity = entityCache.get(newRef.getId());
							
							if (cachedNewReferredEntity != null && !cachedNewReferredEntity.isReference()) {
								final Collection<FXEntityReference> newReferringEntities = new LinkedHashSet<>(cachedNewReferredEntity.getReferringEntities());
								
								newReferringEntities.add(cachedEntity);
								cachedNewReferredEntity.setReferringEntities(newReferringEntities);
							}
						}
						
						// update cached entity 
						cachedEntity.assign(entity);
					}
				}
			}
		});
	}
}