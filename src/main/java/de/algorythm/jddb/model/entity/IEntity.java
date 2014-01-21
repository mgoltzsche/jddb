package de.algorythm.jddb.model.entity;

import java.util.ArrayList;
import java.util.Collection;

public interface IEntity<P extends IPropertyValue<?,REF>, REF extends IEntityReference> extends IEntityReference {

	ArrayList<P> getValues();
	void setValues(ArrayList<P> values);
	Collection<REF> getReferringEntities();
	void setReferringEntities(Collection<REF> referringEntities);
}