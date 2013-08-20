package de.algorythm.jdoe.model.entity;

import java.util.Collection;

public interface IEntity extends IEntityReference {

	boolean isChanged();
	Collection<? extends IPropertyValue<?>> getValues();
	Iterable<? extends IEntityReference> getReferencingEntities();
}