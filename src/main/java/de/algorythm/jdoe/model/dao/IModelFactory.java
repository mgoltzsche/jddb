package de.algorythm.jdoe.model.dao;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.MEntityType;

public interface IModelFactory<V extends IEntity<P,REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> {

	V createNewEntity(MEntityType type);
	V createEntity(IPropertyValueLoader<REF> loader);
	REF createEntityReference(IPropertyValueLoader<REF> loader);
}