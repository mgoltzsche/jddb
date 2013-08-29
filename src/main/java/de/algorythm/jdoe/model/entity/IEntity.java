package de.algorythm.jdoe.model.entity;

import java.util.ArrayList;

public interface IEntity<P extends IPropertyValue<?,REF>, REF extends IEntityReference> extends IEntityReference {

	boolean isChanged();
	void pristine();
	ArrayList<P> getValues();
	void setValues(ArrayList<P> values);
	Iterable<REF> getReferencingEntities();
}