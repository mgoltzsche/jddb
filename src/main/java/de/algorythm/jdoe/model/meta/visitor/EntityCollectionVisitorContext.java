package de.algorythm.jdoe.model.meta.visitor;

import java.util.Collection;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class EntityCollectionVisitorContext extends PropertyVisitorContext {

	@SuppressWarnings("unchecked")
	@Override
	public void doWithPropertyValue(final IPropertyValue propertyValue,
			final IPropertyValueVisitor visitor) {
		visitor.doWithEntityCollection(propertyValue, (Collection<IEntity>) propertyValue.getValue());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String toString(final IPropertyValue propertyValue) {
		final Collection<IEntity> entities = (Collection<IEntity>) propertyValue.getValue();
		
		return String.valueOf(entities.size());
	}
}