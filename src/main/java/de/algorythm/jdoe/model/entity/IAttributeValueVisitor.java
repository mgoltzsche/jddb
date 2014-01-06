package de.algorythm.jdoe.model.entity;

import java.util.Date;

public interface IAttributeValueVisitor {

	void doWithBoolean(IPropertyValue<Boolean,?> propertyValue);
	void doWithDecimal(IPropertyValue<Long,?> propertyValue);
	void doWithReal(IPropertyValue<Double,?> propertyValue);
	void doWithDate(IPropertyValue<Date,?> propertyValue);
	void doWithString(IPropertyValue<String,?> propertyValue);
	void doWithText(IPropertyValue<String,?> propertyValue);
	void doWithFile(IPropertyValue<String,?> propertyValue);
}
