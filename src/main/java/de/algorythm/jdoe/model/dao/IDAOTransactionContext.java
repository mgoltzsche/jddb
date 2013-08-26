package de.algorythm.jdoe.model.dao;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public interface IDAOTransactionContext<REF extends IEntityReference, P extends IPropertyValue<?, REF>> {

	void save(IEntity<REF,P> entity);
	void delete(IEntityReference entity);
}
