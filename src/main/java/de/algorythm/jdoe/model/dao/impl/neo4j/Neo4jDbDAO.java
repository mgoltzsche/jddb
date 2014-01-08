package de.algorythm.jdoe.model.dao.impl.neo4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;

import de.algorythm.jdoe.model.dao.IModelFactory;
import de.algorythm.jdoe.model.dao.impl.ArchiveManager;
import de.algorythm.jdoe.model.dao.impl.blueprints.BlueprintsDAO;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class Neo4jDbDAO<V extends IEntity<P,REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> extends BlueprintsDAO<V,P,REF> {

	static private final Logger LOG = LoggerFactory.getLogger(Neo4jDbDAO.class);
	
	
	public Neo4jDbDAO(final IModelFactory<V, P, REF> modelFactory, final ArchiveManager archiveManager) {
		super(modelFactory, archiveManager);
	}
	
	@Override
	protected void initGraphAndSearchIndex() {
		final Neo4jGraph g = new Neo4jGraph(tmpDbDirectory.getAbsolutePath());
		
		removeInitialDemoNode(g);
		
		g.createKeyIndex(TYPE_FIELD, Vertex.class);
		g.createKeyIndex(ID, Vertex.class);
		
		searchIndex = g.getIndex(SEARCH_INDEX, Vertex.class);
		
		if (searchIndex == null) {
			searchIndex = g.createIndex(SEARCH_INDEX, Vertex.class);
			LOG.debug("New search index created");
		}
		
		graph = g;
	}

	private void removeInitialDemoNode(final Neo4jGraph g) {
		Vertex first = null;
		
		for (Vertex v : g.getVertices()) {
			if (v == null)
				throw new IllegalStateException("Nullvertex");
			if (first == null)
				first = v;
			else
				return;
		}
		
		if (first != null && first.getPropertyKeys().isEmpty())
			first.remove();
	}
}
