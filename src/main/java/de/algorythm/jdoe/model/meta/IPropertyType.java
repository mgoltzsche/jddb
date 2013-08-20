package de.algorythm.jdoe.model.meta;

import de.algorythm.jdoe.model.dao.IPropertyValueFactory;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public interface IPropertyType<V> {
	
	String getLabel();
	boolean isUserDefined();
	<P extends IPropertyValue<?>> P createPropertyValue(Property property, IPropertyValueFactory<P> factory);
	boolean isConform(IPropertyType<?> type);
}
