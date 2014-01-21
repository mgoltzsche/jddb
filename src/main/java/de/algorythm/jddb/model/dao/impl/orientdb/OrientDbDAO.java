package de.algorythm.jddb.model.dao.impl.orientdb;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.algorythm.jddb.model.dao.IModelFactory;
import de.algorythm.jddb.model.dao.impl.blueprints.BlueprintsDAO;
import de.algorythm.jddb.model.entity.IEntity;
import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;

@Singleton
public class OrientDbDAO<V extends IEntity<P,REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> extends BlueprintsDAO<V,P,REF> {
	
	static private final Logger log = LoggerFactory.getLogger(OrientDbDAO.class);
	
	private OrientGraph graph;
	
	public OrientDbDAO(final IModelFactory<V, P, REF> modelFactory) throws URISyntaxException {
		super(modelFactory);
		OGlobalConfiguration.STORAGE_KEEP_OPEN.setValue(Boolean.FALSE);
	}
	
	@Override
	protected TransactionalGraph openGraph(final File dbDirectory) {
		graph = new OrientGraph("local:" + dbDirectory.getAbsolutePath());
		
		graph.setUseLightweightEdges(true);
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
			log.debug("New search index created");
		}
		
		return searchIndex;
	}
	
	@Override
	protected void dropSearchIndex() {
		graph.dropIndex(SEARCH_INDEX);
	}
}
