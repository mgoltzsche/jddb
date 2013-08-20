package de.algorythm.jdoe.model.entity;

import java.util.Collection;
import java.util.Date;

public interface IPropertyValueVisitor<ENTITYREF extends IEntityReference> {

	void doWithAssociation(IPropertyValue<ENTITYREF> propertyValue);
	void doWithAssociations(IPropertyValue<Collection<ENTITYREF>> propertyValue);
	void doWithBoolean(IPropertyValue<Boolean> propertyValue);
	void doWithDecimal(IPropertyValue<Long> propertyValue);
	void doWithReal(IPropertyValue<Double> propertyValue);
	void doWithDate(IPropertyValue<Date> propertyValue);
	void doWithString(IPropertyValue<String> propertyValue);
	void doWithText(IPropertyValue<String> propertyValue);
}