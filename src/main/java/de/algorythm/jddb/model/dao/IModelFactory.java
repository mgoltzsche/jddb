package de.algorythm.jddb.model.dao;

import de.algorythm.jddb.model.entity.IEntity;
import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.meta.MEntityType;

public interface IModelFactory<V extends IEntity<P,REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> {

	V createNewEntity(MEntityType type);
	V createEntity(IPropertyValueLoader<REF> loader);
	REF createEntityReference(IPropertyValueLoader<REF> loader);
}