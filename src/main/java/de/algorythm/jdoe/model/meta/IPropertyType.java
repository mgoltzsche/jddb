package de.algorythm.jdoe.model.meta;

import de.algorythm.jdoe.model.entity.IPropertyValue;

public interface IPropertyType {
	
	String getLabel();
	boolean isUserDefined();
	IPropertyValue createPropertyValue(Property property);
}
