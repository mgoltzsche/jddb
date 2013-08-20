package de.algorythm.jdoe.ui.jfx.model.factory;

import java.util.ArrayList;
import java.util.Collection;

import de.algorythm.jdoe.model.dao.IModelFactory;
import de.algorythm.jdoe.model.entity.impl.propertyValue.Associations;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.IPropertyType;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.FXPropertyValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociations;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;

public class FXModelFactory implements IModelFactory<FXEntity, FXEntityReference, IFXPropertyValue<?>> {

	@Override
	public FXEntity createTransientEntity(EntityType type,
			ArrayList<FXPropertyValue<?>> values) {
		return new FXEntity(type, values);
	}

	@Override
	public FXEntity createEntity(String id, EntityType type,
			ArrayList<FXPropertyValue<?>> values,
			Collection<FXEntityReference> referringEntities) {
		return new FXEntity(id, type, values, referringEntities);
	}

	@Override
	public FXEntityReference createEntityReference(String id, EntityType type,
			ArrayList<FXPropertyValue<?>> values) {
		return new FXEntity(id, type, values);
	}

	@Override
	public <V> FXAttributeValue<V> createPropertyValue(final Property property,
			final IPropertyType<V> type) {
		return new FXAttributeValue(property, type);
	}

	@Override
	public IFXPropertyValue<FXEntityReference> createAssociationValue(final Property property) {
		return new FXPropertyValue<FXEntityReference>(property, property.getType());
	}

	@Override
	public IFXPropertyValue<Collection<FXEntityReference>> createAssociationsValue(final Property property) {
		return new FXAssociations(property);
	}
}