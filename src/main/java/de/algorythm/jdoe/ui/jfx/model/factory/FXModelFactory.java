package de.algorythm.jdoe.ui.jfx.model.factory;

import java.util.ArrayList;
import java.util.Collection;

import de.algorythm.jdoe.model.dao.IModelFactory;
import de.algorythm.jdoe.model.dao.IPropertyValueLoader;
import de.algorythm.jdoe.model.entity.IPropertyValueFactory;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociation;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociations;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAttribute;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;

public class FXModelFactory implements IModelFactory<FXEntity, IFXPropertyValue<?>, FXEntityReference>, IPropertyValueFactory<IFXPropertyValue<?>, FXEntityReference> {

	@Override
	public FXEntity createNewEntity(final EntityType type) {
		final FXEntity entity = new FXEntity(type);
		final Collection<Property> properties = type.getProperties();
		final ArrayList<IFXPropertyValue<?>> values = new ArrayList<>(properties.size());
		
		for (Property property : type.getProperties())
			values.add(property.createPropertyValue(this));
		
		entity.setValues(values);
		
		return entity;
	}

	@Override
	public FXEntity createEntity(final IPropertyValueLoader<FXEntityReference> loader) {
		final String id = loader.getId();
		final EntityType type = loader.getType();
		final FXEntity entity = new FXEntity(id, type);
		
		final Collection<Property> properties = type.getProperties();
		final ArrayList<IFXPropertyValue<?>> propertyValues = new ArrayList<>(properties.size());
		
		for (Property property : type.getProperties()) {
			final IFXPropertyValue<?> propertyValue = property.createPropertyValue(this);
			
			loader.load(propertyValue);
			propertyValues.add(propertyValue);
		}
		
		entity.setValues(propertyValues);
		entity.setReferringEntities(loader.loadReferringEntities());
		
		return entity;
	}

	@Override
	public FXEntityReference createEntityReference(final IPropertyValueLoader<FXEntityReference> loader) {
		final String id = loader.getId();
		final EntityType type = loader.getType();
		final FXEntity entityRef = new FXEntity(id, type);
		final Collection<Property> properties = type.getProperties();
		final ArrayList<IFXPropertyValue<?>> values = new ArrayList<>(properties.size());
		
		for (Property property : type.getProperties()) {
			if (!property.getType().isUserDefined()) {
				final IFXPropertyValue<?> propertyValue = property.createPropertyValue(this);
				
				loader.load(propertyValue);
				values.add(propertyValue);
			}
		}
		
		entityRef.setValues(values);
		
		return entityRef;
	}

	@Override
	public <V> IFXPropertyValue<V> createAttributeValue(final Property property,
			final AbstractAttributeType<V> type) {
		return new FXAttribute<V>(property, type);
	}

	@Override
	public IFXPropertyValue<FXEntityReference> createAssociationValue(final Property property) {
		return new FXAssociation(property);
	}

	@Override
	public IFXPropertyValue<Collection<FXEntityReference>> createAssociationsValue(final Property property) {
		return new FXAssociations(property);
	}
}