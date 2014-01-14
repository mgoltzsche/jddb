package de.algorythm.jdoe.model.dao.impl.blueprints.propertyVisitor;

import java.util.Collection;
import java.util.Map;

import com.tinkerpop.blueprints.Vertex;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public interface IDAOVisitorContext<V extends IEntity<P, REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> {

	REF createEntityReference(Vertex vertex);
	Collection<REF> loadReferringEntities(String entityId, Vertex vertex);
	Vertex findVertex(IEntityReference entityRef);
	Vertex save(V entity, Map<V, Vertex> savedEntities);
	void delete(REF entityRef);
}
