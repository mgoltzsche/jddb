package de.algorythm.jddb.model.dao;

import java.io.Serializable;
import java.util.Collection;

import de.algorythm.jddb.model.meta.MEntityType;

public interface ISchema extends Serializable {

	Collection<MEntityType> getTypes();
	MEntityType getTypeByName(String typeName);
	boolean isKnownType(String typeName);
}
