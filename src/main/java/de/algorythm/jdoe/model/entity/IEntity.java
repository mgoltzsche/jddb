package de.algorythm.jdoe.model.entity;

import java.util.Collection;

public interface IEntity<P extends IPropertyValue<?,REF>, REF extends IEntityReference> extends IEntityReference {

	boolean isChanged();
	void pristine();
	Collection<P> getValues();
	Iterable<REF> getReferencingEntities();
}