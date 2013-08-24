package de.algorythm.jdoe.model.entity;

import java.util.Collection;
import java.util.Date;

public interface IPropertyValueVisitor<REF extends IEntityReference> extends IAttributeValueVisitor {

	void doWithAssociation(IPropertyValue<REF,REF> propertyValue);
	void doWithAssociations(IPropertyValue<Collection<REF>,REF> propertyValue);
}