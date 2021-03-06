package de.algorythm.jddb.model.entity;

import java.io.Serializable;

import de.algorythm.jddb.model.meta.MProperty;

public interface IPropertyValue<V, REF extends IEntityReference> extends Serializable {
	
	MProperty getProperty();
	void visit(IPropertyValueVisitor<REF> visitor);
	V getValue();
	void setValue(V v);
	void toString(StringBuilder sb);
}