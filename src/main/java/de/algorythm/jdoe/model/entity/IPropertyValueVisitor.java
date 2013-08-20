package de.algorythm.jdoe.model.entity;

import java.util.Collection;

public interface IPropertyValueVisitor<E extends IEntityReference> extends IAttributeValueVisitor {

	void doWithAssociation(IPropertyValue<E,E> propertyValue);
	void doWithAssociations(IPropertyValue<Collection<E>,E> propertyValue);
}