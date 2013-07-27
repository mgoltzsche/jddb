package de.algorythm.jdoe.model.entity;

import java.io.Serializable;

import de.algorythm.jdoe.model.meta.Property;

public interface IPropertyValue extends Serializable {
	
	Property getProperty();
	boolean isChanged();
	void doWithValue(IPropertyValueVisitor visitor);
}