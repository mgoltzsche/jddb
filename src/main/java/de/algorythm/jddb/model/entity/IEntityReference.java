package de.algorythm.jddb.model.entity;

import java.io.Serializable;

import de.algorythm.jddb.model.meta.MEntityType;

public interface IEntityReference extends Serializable {

	String getId();
	MEntityType getType();
	void toString(StringBuilder sb);
}
