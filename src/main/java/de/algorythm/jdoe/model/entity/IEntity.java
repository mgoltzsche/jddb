package de.algorythm.jdoe.model.entity;

import java.io.Serializable;
import java.util.Collection;

import de.algorythm.jdoe.model.meta.EntityType;

public interface IEntity extends Serializable {

	String getId();
	boolean isPersisted();
	EntityType getType();
	Collection<IPropertyValue<?>> getValues();
	Iterable<IEntity> getReferencingEntities();
}