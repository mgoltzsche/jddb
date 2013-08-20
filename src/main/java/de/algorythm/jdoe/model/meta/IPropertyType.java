package de.algorythm.jdoe.model.meta;

import de.algorythm.jdoe.model.dao.IPropertyValueFactory;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;

public interface IPropertyType<V> {
	
	String getLabel();
	boolean isUserDefined();
	<P extends IPropertyValue<?>> P createPropertyValue(Property property, IPropertyValueFactory<P> factory);
	void doWithPropertyValue(IPropertyValue<V> value, IPropertyValueVisitor visitor);
	boolean valueChanged(V oldValue, V newValue);
	boolean isConform(IPropertyType<?> type);
	void valueToString(V value, StringBuilder sb);
}
