package de.algorythm.jddb.model.entity;

import java.util.Collection;
import java.util.Date;

public interface IPropertyValueVisitor<REF extends IEntityReference> {

	void doWithAssociation(IPropertyValue<REF,REF> propertyValue);
	void doWithAssociations(IPropertyValue<Collection<REF>,REF> propertyValue);
	void doWithBoolean(IPropertyValue<Boolean,?> propertyValue);
	void doWithDecimal(IPropertyValue<Long,?> propertyValue);
	void doWithReal(IPropertyValue<Double,?> propertyValue);
	void doWithDate(IPropertyValue<Date,?> propertyValue);
	void doWithString(IPropertyValue<String,?> propertyValue);
	void doWithText(IPropertyValue<String,?> propertyValue);
	void doWithFile(IPropertyValue<String,?> propertyValue);
}