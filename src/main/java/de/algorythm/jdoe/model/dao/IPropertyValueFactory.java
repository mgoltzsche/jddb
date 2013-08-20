package de.algorythm.jdoe.model.dao;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.IPropertyType;
import de.algorythm.jdoe.model.meta.Property;

public interface IPropertyValueFactory<P extends IPropertyValue<?>> {

	P createAssociationValue(Property property);
	P createAssociationsValue(Property property);
	<V> P createPropertyValue(Property property, IPropertyType<V> type);
}
