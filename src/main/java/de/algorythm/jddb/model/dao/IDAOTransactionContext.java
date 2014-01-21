package de.algorythm.jddb.model.dao;

import de.algorythm.jddb.model.entity.IEntity;
import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;

public interface IDAOTransactionContext<V extends IEntity<P, REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> {

	void save(V entity);
	void delete(REF entityRef);
}
