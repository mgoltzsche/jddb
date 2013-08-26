package de.algorythm.jdoe.model.dao.impl.orientdb;

import java.util.Map;

import com.tinkerpop.blueprints.Vertex;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public interface IDAOVisitorContext<REF extends IEntityReference, P extends IPropertyValue<?, REF>> {

	REF createEntityReference(Vertex vertex);
	Vertex findVertex(IEntityReference entityRef);
	Vertex save(IEntity<REF,P> entity, Map<IEntity<REF,P>, Vertex> savedEntities);
	void delete(IEntityReference entity);
}
