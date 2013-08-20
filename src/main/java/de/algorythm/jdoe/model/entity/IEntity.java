package de.algorythm.jdoe.model.entity;

import java.util.Collection;

public interface IEntity<E extends IEntityReference> extends IEntityReference {

	boolean isChanged();
	Collection<? extends IPropertyValue<?,E>> getValues();
	Iterable<E> getReferencingEntities();
}