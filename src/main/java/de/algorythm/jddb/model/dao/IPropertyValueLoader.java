package de.algorythm.jddb.model.dao;

import java.util.Collection;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.meta.MEntityType;

public interface IPropertyValueLoader<REF extends IEntityReference> {

	String getId();
	MEntityType getType();
	Collection<REF> loadReferringEntities();
	void load(IPropertyValue<?,REF> propertyValue);
}
