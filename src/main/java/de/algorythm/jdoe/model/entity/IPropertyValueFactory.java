package de.algorythm.jdoe.model.entity;

import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;

public interface IPropertyValueFactory<P extends IPropertyValue<?,REF>, REF extends IEntityReference> {

	P createAssociationValue(Property property);
	P createAssociationsValue(Property property);
	<V extends Comparable<V>> P createAttributeValue(Property property, AbstractAttributeType<V> type);
}
