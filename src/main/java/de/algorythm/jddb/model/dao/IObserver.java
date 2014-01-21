package de.algorythm.jddb.model.dao;

import de.algorythm.jddb.model.entity.IEntity;
import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;

public interface IObserver<V extends IEntity<P,REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> {

	public void update(ModelChange<V, P, REF> change);
}
