package de.algorythm.jdoe.model.dao;

import java.util.ArrayList;
import java.util.Collection;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueFactory;
import de.algorythm.jdoe.model.meta.EntityType;

public interface IModelFactory<V extends IEntity<P,REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> extends IPropertyValueFactory<REF,P> {

	V createTransientEntity(EntityType type, ArrayList<P> values);
	V createEntity(String id, EntityType type, ArrayList<P> values, Collection<REF> referringEntities);
	REF createEntityReference(String id, EntityType type, ArrayList<P> values);
}