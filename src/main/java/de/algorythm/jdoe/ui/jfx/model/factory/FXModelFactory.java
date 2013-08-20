package de.algorythm.jdoe.ui.jfx.model.factory;

import java.util.ArrayList;
import java.util.Collection;

import de.algorythm.jdoe.model.dao.IModelFactory;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.IPropertyType;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.FXPropertyValue;

public class FXModelFactory implements IModelFactory<FXEntity, FXEntityReference, FXPropertyValue<?>> {

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
	public <V> FXPropertyValue<?> createPropertyValue(Property property,
			IPropertyType<V> type) {
		return new FXPropertyValue<V>(property, type);
	}

	@Override
	public FXPropertyValue<FXEntityReference> createAssociationValue(Property property) {
		return new FXPropertyValue<FXEntityReference>(property, property.getType());
	}

	@Override
	public FXPropertyValue<Collection<FXEntityReference>> createAssociationsValue(Property property) {
		return new FXPropertyValue<Collection<FXEntityReference>>(property, property.getType());
	}
}