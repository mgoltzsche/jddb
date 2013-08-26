package de.algorythm.jdoe.model.entity;

import java.io.Serializable;

import de.algorythm.jdoe.model.meta.EntityType;

public interface IEntityReference extends Serializable {

	String getId();
	EntityType getType();
	void toString(StringBuilder sb);
}
