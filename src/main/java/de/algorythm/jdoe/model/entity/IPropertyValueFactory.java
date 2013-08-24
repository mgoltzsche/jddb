package de.algorythm.jdoe.model.entity;

import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;

public interface IPropertyValueFactory<REF extends IEntityReference, P extends IPropertyValue<?,REF>> {

	P createAssociationValue(Property property);
	P createAssociationsValue(Property property);
	<V> P createAttributeValue(Property property, AbstractAttributeType<V> type);
}
