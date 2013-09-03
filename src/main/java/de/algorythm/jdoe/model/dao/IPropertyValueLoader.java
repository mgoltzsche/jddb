package de.algorythm.jdoe.model.dao;

import java.util.Collection;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.EntityType;

public interface IPropertyValueLoader<REF extends IEntityReference> {

	String getId();
	EntityType getType();
	Collection<REF> loadReferringEntities();
	void load(IPropertyValue<?, REF> propertyValue);
}
