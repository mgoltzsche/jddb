package de.algorythm.jdoe.model.dao;

import java.io.IOException;
import java.util.Collection;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.Schema;

public interface IDAO {
	
	void open() throws IOException;
	void close() throws IOException;
	Schema getSchema();
	void setSchema(Schema schema) throws IOException;
	Collection<IEntity> list(EntityType type);
	IEntity create(EntityType type);
	void save(IEntity entity);
	void delete(IEntity entity);
}
