package de.algorythm.jdoe.model.meta.visitor;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.IPropertyType;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.attributeTypes.AbstractAttributeType;

public abstract class PropertyVisitorContext {

	static public PropertyVisitorContext createFor(final Property property) {
		final IPropertyType propertyType = property.getType();
		
		if (propertyType.isUserDefined())
			return property.isCollection()
				? new EntityCollectionVisitorContext()
				: new EntityVisitorContext();
		else
			return (AbstractAttributeType) property.getType();
	}
	
	public abstract void doWithPropertyValue(IPropertyValue propertyValue, IPropertyValueVisitor visitor);
	public abstract String toString(IPropertyValue propertyValue);
}