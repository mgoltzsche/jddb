package de.algorythm.jdoe.model.entity;

import java.util.Collection;

public interface IPropertyValueVisitor extends IAttributeValueVisitor {

	void doWithAssociation(IPropertyValue<IEntityReference> propertyValue);
	void doWithAssociations(IPropertyValue<Collection<IEntityReference>> propertyValue);
}