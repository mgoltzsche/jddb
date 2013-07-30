package de.algorythm.jdoe.model.entity;

import java.io.Serializable;

import de.algorythm.jdoe.model.meta.Property;

public interface IPropertyValue<V> extends Serializable {
	
	Property getProperty();
	boolean isChanged();
	void doWithValue(IPropertyValueVisitor visitor);
	V getValue();
	void setValue(V v);
}