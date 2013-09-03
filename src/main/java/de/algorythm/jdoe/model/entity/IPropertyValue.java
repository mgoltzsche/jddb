package de.algorythm.jdoe.model.entity;

import java.io.Serializable;

import de.algorythm.jdoe.model.meta.Property;

public interface IPropertyValue<V,REF extends IEntityReference> extends Serializable {
	
	Property getProperty();
	boolean isChanged();
	void setChanged(boolean changed);
	void visit(IPropertyValueVisitor<REF> visitor);
	V getValue();
	void setValue(V v);
	void setChangeHandler(IPropertyValueChangeHandler changeHandler);
	void toString(StringBuilder sb);
}