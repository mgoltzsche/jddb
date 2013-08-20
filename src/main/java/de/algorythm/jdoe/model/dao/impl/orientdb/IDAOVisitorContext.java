package de.algorythm.jdoe.model.dao.impl.orientdb;

import java.util.Collection;

import com.tinkerpop.blueprints.Vertex;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;

public interface IDAOVisitorContext<E extends IEntityReference> {

	//IEntity createEntity(Vertex vertex);
	E createEntityReference(Vertex vertex);
	Vertex findVertex(IEntityReference entityRef);
	Vertex saveInTransaction(IEntity<E> entity, Collection<IEntity<E>> savedEntities);
	void deleteInTransaction(IEntityReference entity);
}
