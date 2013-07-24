package de.algorythm.jdoe.model.entity;

import java.io.Serializable;

import de.algorythm.jdoe.model.meta.Property;

public interface IPropertyValue extends Serializable {
	
	Property getProperty();
	Object getValue();
	void setValue(Object value);
	boolean isChanged();
}