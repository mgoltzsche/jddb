package de.algorythm.jdoe.model.entity;

import java.io.Serializable;

import de.algorythm.jdoe.model.meta.EntityType;

public interface IEntity extends Serializable {

	EntityType getType();
	Iterable<IPropertyValue> getValues();
	IPropertyValue getValue(int index);
	Iterable<IEntity> getReferencingEntities();
}