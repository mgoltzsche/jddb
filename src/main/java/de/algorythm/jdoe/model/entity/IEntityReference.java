package de.algorythm.jdoe.model.entity;

import java.io.Serializable;

import de.algorythm.jdoe.model.meta.MEntityType;

public interface IEntityReference extends Serializable {

	String getId();
	MEntityType getType();
	void toString(StringBuilder sb);
}
