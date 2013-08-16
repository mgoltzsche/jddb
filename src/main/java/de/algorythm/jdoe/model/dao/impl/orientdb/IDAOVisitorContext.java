package de.algorythm.jdoe.model.dao.impl.orientdb;

import java.util.Collection;

import com.tinkerpop.blueprints.Vertex;

import de.algorythm.jdoe.model.entity.IEntity;

public interface IDAOVisitorContext {

	IEntity createEntity(Vertex vertex);
	void saveInTransaction(IEntity entity, Collection<Entity> savedEntities);
	void deleteInTransaction(IEntity entity);
}
