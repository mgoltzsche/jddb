package de.algorythm.jdoe.model.dao;

import java.io.IOException;
import java.util.Set;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.Schema;

public interface IDAO {
	
	void open() throws IOException;
	void close() throws IOException;
	void addObserver(IObserver observer);
	void removeObserver(IObserver observer);
	Schema getSchema();
	void setSchema(Schema schema) throws IOException;
	Set<IEntity> list(EntityType type);
	Set<IEntity> list(EntityType type, String search);
	IEntity createEntity(EntityType type);
	void save(IEntity entity);
	void delete(IEntity entity);
	boolean update(IEntity entity);
	boolean exists(IEntity entity);
}
