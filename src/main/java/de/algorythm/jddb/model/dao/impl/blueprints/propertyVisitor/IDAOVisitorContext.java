package de.algorythm.jddb.model.dao.impl.blueprints.propertyVisitor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.Vertex;

import de.algorythm.jddb.model.entity.IEntity;
import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;

public interface IDAOVisitorContext<V extends IEntity<P, REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> {

	REF createEntityReference(Vertex vertex);
	Collection<REF> loadReferringEntities(String entityId, Vertex vertex);
	Vertex findVertex(IEntityReference entityRef);
	Vertex save(V entity, Map<V, Vertex> savedEntities);
	void delete(REF entityRef, Set<REF> deletedEntities);
}
