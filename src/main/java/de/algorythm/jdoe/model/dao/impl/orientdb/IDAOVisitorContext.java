package de.algorythm.jdoe.model.dao.impl.orientdb;

import java.util.Collection;

import com.tinkerpop.blueprints.Vertex;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;

public interface IDAOVisitorContext {

	//IEntity createEntity(Vertex vertex);
	IEntityReference createEntityReference(Vertex vertex);
	Vertex findVertex(IEntityReference entityRef);
	Vertex saveInTransaction(IEntity entity, Collection<IEntity> savedEntities);
	void deleteInTransaction(IEntityReference entity);
}
