package de.algorythm.jdoe.model.dao;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public interface IDAOTransactionContext<V extends IEntity<P, REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> {

	void save(V entity);
	void delete(REF entityRef);
}
