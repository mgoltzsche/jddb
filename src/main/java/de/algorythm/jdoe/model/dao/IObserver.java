package de.algorythm.jdoe.model.dao;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public interface IObserver<V extends IEntity<P,REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> {

	public void update(ModelChange<V, P, REF> change);
}
