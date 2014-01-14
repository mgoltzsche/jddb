package de.algorythm.jdoe.ui.jfx.model.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import de.algorythm.jdoe.model.meta.MEntityType;
import de.algorythm.jdoe.model.meta.MProperty;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.IFXPropertyValueChangeHandler;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.BooleanFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.DateFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.DecimalFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociation;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociations;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FileFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.RealFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.StringFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.TextFXAttributeValue;

public class FXModelFactory implements IModelFactory<FXEntity, IFXPropertyValue<?>, FXEntityReference>, IPropertyValueFactory<IFXPropertyValue<?>>, IObserver<FXEntity, IFXPropertyValue<?>, FXEntityReference> {

	private IObjectCache<FXEntity> entityCache;
	
	public void init(final IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao, final IObjectCache<FXEntity> entityCache) {
		this.entityCache = entityCache;
		
		dao.addObserver(this);
	}
	
	@Override
	public FXEntity createNewEntity(final MEntityType type) {
		final FXEntity entity = new FXEntity(type);
		final Collection<MProperty> properties = type.getProperties();
		final ArrayList<IFXPropertyValue<?>> values = new ArrayList<>(properties.size());
		
		for (MProperty property : properties)
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
		final MEntityType type = loader.getType();
		final Collection<MProperty> properties = type.getProperties();
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
			
			for (MProperty property : properties) {
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
	public IFXPropertyValue<?> createAssociationValue(final MProperty property) {
		return new FXAssociation(property);
	}

	@Override
	public IFXPropertyValue<?> createAssociationsValue(final MProperty property) {
		return new FXAssociations(property);
	}
	
	@Override
	public IFXPropertyValue<?> createBooleanAttributeValue(MProperty property,
			AbstractAttributeType<Boolean> type) {
		return new BooleanFXAttributeValue(property, type);
	}

	@Override
	public IFXPropertyValue<?> createDecimalAttributeValue(MProperty property,
			AbstractAttributeType<Long> type) {
		return new DecimalFXAttributeValue(property, type);
	}

	@Override
	public IFXPropertyValue<?> createRealAttributeValue(MProperty property,
			AbstractAttributeType<Double> type) {
		return new RealFXAttributeValue(property, type);
	}

	@Override
	public IFXPropertyValue<?> createDateAttributeValue(MProperty property,
			AbstractAttributeType<Date> type) {
		return new DateFXAttributeValue(property, type);
	}

	@Override
	public IFXPropertyValue<?> createStringAttributeValue(MProperty property,
			AbstractAttributeType<String> type) {
		return new StringFXAttributeValue(property, type);
	}

	@Override
	public IFXPropertyValue<?> createTextAttributeValue(MProperty property,
			AbstractAttributeType<String> type) {
		return new TextFXAttributeValue(property, type);
	}

	@Override
	public IFXPropertyValue<?> createFileAttributeValue(MProperty property,
			AbstractAttributeType<String> type) {
		return new FileFXAttributeValue(property, type);
	}
	
	@Override
	public void update(final ModelChange<FXEntity,IFXPropertyValue<?>,FXEntityReference> change) {
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