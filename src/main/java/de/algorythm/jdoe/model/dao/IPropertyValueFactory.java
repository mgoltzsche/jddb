package de.algorythm.jdoe.model.dao;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;

public interface IPropertyValueFactory<E extends IEntityReference, P extends IPropertyValue<?,E>> {

	P createAssociationValue(Property property);
	P createAssociationsValue(Property property);
	<V> P createAttributeValue(Property property, AbstractAttributeType<V> type);
}
