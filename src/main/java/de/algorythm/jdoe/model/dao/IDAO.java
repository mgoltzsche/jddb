package de.algorythm.jdoe.model.dao;

import java.io.IOException;
import java.util.Set;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.Schema;

public interface IDAO<V extends IEntity<REF,P>, P extends IPropertyValue<?, REF>, REF extends IEntityReference> {
	
	void open() throws IOException;
	void close() throws IOException;
	void addObserver(IObserver observer);
	void removeObserver(IObserver observer);
	Schema getSchema();
	void setSchema(Schema schema) throws IOException;
	Set<V> list(EntityType type);
	Set<V> list(EntityType type, String search);
	V createEntity(EntityType type);
	V find(IEntityReference entityRef);
	boolean exists(IEntityReference entityRef);
	void transaction(Procedure1<IDAOTransactionContext<REF,P>> transaction);
}
