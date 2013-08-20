package de.algorythm.jdoe.model.entity;

import java.util.Collection;

public interface IEntity<REF extends IEntityReference, P extends IPropertyValue<?,REF>> extends IEntityReference {

	boolean isChanged();
	Collection<P> getValues();
	Iterable<REF> getReferencingEntities();
}