package de.algorythm.jdoe.ui.jfx.model.factory;

import java.util.ArrayList;
import java.util.Collection;

import de.algorythm.jdoe.model.dao.IModelFactory;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociation;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociations;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAttribute;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;

public class FXModelFactory implements IModelFactory<FXEntity, IFXPropertyValue<?>, FXEntityReference> {

	@Override
	public FXEntity createTransientEntity(EntityType type,
			ArrayList<IFXPropertyValue<?>> values) {
		final FXEntity entity = new FXEntity(type);
		
		entity.setValues(values);
		
		return entity;
	}

	@Override
	public FXEntity createEntity(String id, EntityType type,
			ArrayList<IFXPropertyValue<?>> values,
			Collection<FXEntityReference> referringEntities) {
		final FXEntity entity = new FXEntity(id, type, referringEntities);
		
		entity.setValues(values);
		
		return entity;
	}

	@Override
	public FXEntityReference createEntityReference(String id, EntityType type,
			ArrayList<IFXPropertyValue<?>> values) {
		final FXEntity entity = new FXEntity(id, type);
		
		entity.setValues(values);
		
		return entity;
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