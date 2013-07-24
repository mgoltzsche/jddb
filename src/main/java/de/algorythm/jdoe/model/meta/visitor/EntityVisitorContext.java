package de.algorythm.jdoe.model.meta.visitor;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class EntityVisitorContext extends PropertyVisitorContext {

	@Override
	public void doWithPropertyValue(final IPropertyValue propertyValue,
			final IPropertyValueVisitor visitor) {
		visitor.doWithEntity(propertyValue, (IEntity) propertyValue.getValue());
	}
	
	@Override
	public String toString(final IPropertyValue propertyValue) {
		final Object value = propertyValue.getValue();
		
		return value == null ? null : value.toString();
	}
}