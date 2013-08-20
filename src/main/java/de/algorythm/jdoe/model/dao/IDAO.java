package de.algorythm.jdoe.model.dao;

import java.io.IOException;
import java.util.Set;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.Schema;

public interface IDAO<VREF extends IEntityReference, V extends IEntity<VREF>> {
	
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
	void save(IEntity<VREF> entity);
	void delete(IEntity<VREF> entity);
	boolean exists(IEntityReference entityRef);
}
