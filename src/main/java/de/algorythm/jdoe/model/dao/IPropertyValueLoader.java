package de.algorythm.jdoe.model.dao;

import java.util.Collection;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.MEntityType;

public interface IPropertyValueLoader<REF extends IEntityReference> {

	String getId();
	MEntityType getType();
	Collection<REF> loadReferringEntities();
	void load(IPropertyValue<?,REF> propertyValue);
}
