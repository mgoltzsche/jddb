package de.algorythm.jdoe.model.dao.impl.orientdb;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.algorythm.jdoe.model.dao.IModelFactory;
import de.algorythm.jdoe.model.dao.impl.blueprints.BlueprintsDAO;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;

@Singleton
public class OrientDbDAO<V extends IEntity<P,REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> extends BlueprintsDAO<V,P,REF> {
	
	static private final Logger LOG = LoggerFactory.getLogger(OrientDbDAO.class);
	
	
	public OrientDbDAO(final IModelFactory<V, P, REF> modelFactory) {
		super(modelFactory);
		OGlobalConfiguration.STORAGE_KEEP_OPEN.setValue(Boolean.FALSE);
	}

	@Override
	protected void initGraphAndSearchIndex() {
		final OrientGraph g = new OrientGraph("local:" + getDbDirectory().getAbsolutePath());
		
		g.setUseLightweightEdges(true);
		g.createKeyIndex(TYPE_FIELD, Vertex.class);
		g.createKeyIndex(ID, Vertex.class);
		
		searchIndex = g.getIndex(SEARCH_INDEX, Vertex.class);
		
		if (searchIndex == null) {
			searchIndex = g.createIndex(SEARCH_INDEX, Vertex.class);
			LOG.debug("New search index created");
		}
		
		graph = g;
	}
}
