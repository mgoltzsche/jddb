package de.algorythm.jddb.model.dao.impl.neo4j;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;

import de.algorythm.jddb.model.dao.IModelFactory;
import de.algorythm.jddb.model.dao.impl.blueprints.BlueprintsDAO;
import de.algorythm.jddb.model.entity.IEntity;
import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;

public class Neo4jDbDAO<V extends IEntity<P,REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> extends BlueprintsDAO<V,P,REF> {

	static private final Logger LOG = LoggerFactory.getLogger(Neo4jDbDAO.class);
	
	private Neo4jGraph graph;
	
	public Neo4jDbDAO(final IModelFactory<V, P, REF> modelFactory) throws URISyntaxException {
		super(modelFactory);
	}
	
	@Override
	protected TransactionalGraph openGraph(final File dbDirectory) {
		graph = new Neo4jGraph(dbDirectory.getAbsolutePath());
		
		removeInitialDemoNode();
		
		graph.createKeyIndex(TYPE_FIELD, Vertex.class);
		graph.createKeyIndex(ID, Vertex.class);
		
		return graph;
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		graph = null;
	}
	
	@Override
	protected Index<Vertex> getOrCreateSearchIndex() {
		Index<Vertex> searchIndex = graph.getIndex(SEARCH_INDEX, Vertex.class);
		
		if (searchIndex == null) {
			searchIndex = graph.createIndex(SEARCH_INDEX, Vertex.class);
			LOG.debug("New search index created");
		}
		
		return searchIndex;
	}
	
	@Override
	protected void dropSearchIndex() {
		graph.dropIndex(SEARCH_INDEX);
	}

	private void removeInitialDemoNode() {
		Vertex first = null;
		
		for (Vertex v : graph.getVertices()) {
			if (first == null)
				first = v;
			else
				return;
		}
		
		if (first != null && first.getPropertyKeys().isEmpty())
			first.remove();
	}
}
