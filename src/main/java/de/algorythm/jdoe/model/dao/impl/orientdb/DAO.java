package de.algorythm.jdoe.model.dao.impl.orientdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.tinkerpop.blueprints.CloseableIterable;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.dao.IModelFactory;
import de.algorythm.jdoe.model.dao.IObserver;
import de.algorythm.jdoe.model.dao.impl.orientdb.propertyVisitor.DeleteVisitor;
import de.algorythm.jdoe.model.dao.impl.orientdb.propertyVisitor.IndexKeywordCollectingVisitor;
import de.algorythm.jdoe.model.dao.impl.orientdb.propertyVisitor.LoadVisitor;
import de.algorythm.jdoe.model.dao.impl.orientdb.propertyVisitor.SaveVisitor;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.entity.impl.AbstractEntity;
import de.algorythm.jdoe.model.entity.impl.propertyValue.AbstractPropertyValue;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.Schema;

@Singleton
public class DAO<V extends IEntity<VREF>, VREF extends IEntityReference, P extends IPropertyValue<?,VREF>> implements IDAO<VREF,V>, IDAOVisitorContext<VREF> {
	
	static private final Logger LOG = LoggerFactory.getLogger(DAO.class);
	
	static private final String TYPE_FIELD = "_type";
	static private final String ID = "_id";
	static private final String SEARCH_INDEX = "searchIndex";
	static private final Pattern WORD_PATTERN = Pattern.compile("[\\w]+");
	
	private final IModelFactory<V, VREF, P> modelFactory;
	private final Yaml yaml = new Yaml();
	private Schema schema;
	private OrientGraph graph;
	private final HashSet<IObserver> observers = new HashSet<>();
	private final HashMap<String, Index<Vertex>> searchIndices = new HashMap<>();
	
	public DAO(IModelFactory<V, VREF, P> modelFactory) {
		this.modelFactory = modelFactory;
	}
	
	public void open() throws IOException {
		loadSchema();
		graph = new OrientGraph("local:graph.db");
		
		graph.setUseLightweightEdges(true);
		graph.createKeyIndex(TYPE_FIELD, Vertex.class);
		graph.createKeyIndex(ID, Vertex.class);
		
		mapIndices();
	}
	
	private void mapIndices() {
		for (EntityType type : schema.getTypes()) {
			final String typeName = type.getName();
			Index<Vertex> typeIndex = graph.getIndex(typeName, Vertex.class);
			
			if (typeIndex == null)
				typeIndex = graph.createIndex(typeName, Vertex.class);
			
			searchIndices.put(typeName, typeIndex);
		}
	}
	
	public void close() {
		if (graph != null)
			graph.shutdown();
	}
	
	private void loadSchema() throws IOException {
		try {
			schema = yaml.loadAs(new FileReader(new File("schema.yaml")), Schema.class);
		} catch(FileNotFoundException e) {
			schema = new Schema();
		}
	}
	
	@Override
	public Schema getSchema() {
		return schema;
	}

	@Override
	public void setSchema(final Schema schema) throws IOException {
		yaml.dump(schema, new FileWriter(new File("schema.yaml")));
		this.schema = schema;
	}

	@Override
	public V createEntity(final EntityType type) {
		final Collection<Property> properties = type.getProperties();
		final ArrayList<P> propertyValues = new ArrayList<>(properties.size());
		
		for (Property property : type.getProperties())
			propertyValues.add(property.createPropertyValue(modelFactory));
		
		return modelFactory.createTransientEntity(type, propertyValues);
	}
	
	@Override
	public VREF createEntityReference(final Vertex vertex) {
		final String id = vertex.<String>getProperty(ID);
		final EntityType type = schema.getTypeByName(vertex.<String>getProperty(TYPE_FIELD));
		final IPropertyValueVisitor<VREF> visitor = new LoadVisitor<>(vertex, this);
		final Collection<Property> properties = type.getProperties();
		final ArrayList<P> propertyValues = new ArrayList<>(properties.size());
		
		for (Property property : type.getProperties()) {
			if (!property.getType().isUserDefined()) {
				final P propertyValue = property.createPropertyValue(modelFactory);
				
				propertyValue.doWithValue(visitor);
				((AbstractPropertyValue<?,VREF>) propertyValue).setChanged(false);
				
				propertyValues.add(propertyValue);
			}
		}
		
		return modelFactory.createEntityReference(id, type, propertyValues);
	}
	
	private V createEntity(final Vertex vertex) {
		final String id = vertex.<String>getProperty(ID);
		final EntityType type = schema.getTypeByName(vertex.<String>getProperty(TYPE_FIELD));
		final IPropertyValueVisitor<VREF> visitor = new LoadVisitor<>(vertex, this);
		final Collection<Property> properties = type.getProperties();
		final ArrayList<P> propertyValues = new ArrayList<>(properties.size());
		
		for (Property property : type.getProperties()) {
			final P propertyValue = property.createPropertyValue(modelFactory);
			
			propertyValue.doWithValue(visitor);
			((AbstractPropertyValue<?,VREF>) propertyValue).setChanged(false);
			
			propertyValues.add(propertyValue);
		}
		
		return modelFactory.createEntity(id, type, propertyValues, referredEntities(vertex));
	}
	
	private Collection<VREF> referredEntities(final Vertex vertex) {
		final LinkedList<VREF> entities = new LinkedList<>();
		
		for (Edge edge : vertex.getEdges(Direction.IN)) {
			final Vertex referringVertex = edge.getVertex(Direction.OUT);
			
			entities.add(createEntityReference(referringVertex));
		}
		
		return entities;
	}
	
	@Override
	public void save(final IEntity<VREF> entity) {
		if (!entity.isChanged())
			return;
		
		final LinkedList<IEntity<VREF>> savedEntities = new LinkedList<>();
		
		try {
			saveInTransaction(entity, savedEntities);
			graph.commit();
		} catch(Throwable e) {
			graph.rollback();
			
			throw e;
		}
		
		// set entity unchanged
		for (IEntity<VREF> savedEntity : savedEntities) {
			((AbstractEntity<VREF>) savedEntity).setTransientInstance(false);
			
			for (IPropertyValue<?,VREF> propertyValue : savedEntity.getValues())
				((AbstractPropertyValue<?,VREF>) propertyValue).setChanged(false);
		}
		
		notifyObservers();
	}
	
	@Override
	public Vertex saveInTransaction(final IEntity<VREF> entity, final Collection<IEntity<VREF>> savedEntities) {
		Vertex vertex = findVertex(entity);
		final Index<Vertex> searchIndex = searchIndices.get(entity.getType().getName());
		final Set<String> oldIndexKeywords;
		
		savedEntities.add(entity);
		
		if (vertex == null) { // create new vertex
			vertex = graph.addVertex(null);
			vertex.setProperty(ID, entity.getId());
			vertex.setProperty(TYPE_FIELD, entity.getType().getName());
			oldIndexKeywords = new HashSet<>();
		} else {
			oldIndexKeywords = createIndexKeywords(createEntity(vertex));
		}
		
		final Set<String> indexKeywords = new HashSet<>();
		final SaveVisitor<VREF> visitor = new SaveVisitor<VREF>(this, vertex, savedEntities, WORD_PATTERN, indexKeywords);
		
		// assign values to vertex
		for (IPropertyValue<?,VREF> propertyValue : entity.getValues())
			propertyValue.doWithValue(visitor);
		
		// update vertex index if keyword changed
		final HashSet<String> newIndexKeywords = new HashSet<>(indexKeywords);
		
		newIndexKeywords.removeAll(oldIndexKeywords);
		oldIndexKeywords.removeAll(indexKeywords);
		
		for (String keyword : oldIndexKeywords) // remove expired keywords
			searchIndex.remove(SEARCH_INDEX, keyword, vertex);
		
		for (String keyword : newIndexKeywords) // save new keywords
			searchIndex.put(SEARCH_INDEX, keyword, vertex);
		
		return vertex;
	}
	
	private Set<String> createIndexKeywords(final V entity) {
		final Set<String> indexKeywords = new HashSet<>();
		final IndexKeywordCollectingVisitor<VREF> visitor = new IndexKeywordCollectingVisitor<>(WORD_PATTERN, indexKeywords);
		
		for (IPropertyValue<?,VREF> propertyValue : entity.getValues())
			propertyValue.doWithValue(visitor);
		
		return indexKeywords;
	}
	
	@Override
	public void delete(final IEntity<VREF> entity) {
		try {
			deleteInTransaction(entity);
			graph.commit();
		} catch(Throwable e) {
			graph.rollback();
			throw e;
		}
		
		notifyObservers();
	}
	
	@Override
	public void deleteInTransaction(final IEntityReference entityRef) {
		final Vertex vertex;
		
		try {
			vertex = findVertex(entityRef);
		} catch(IllegalArgumentException e) {
			LOG.warn("Cannot remove entity " + entityRef + "(" + entityRef.getId() + ") because it doesn't exist");
			return;
		}
		
		final V entity = createEntity(vertex);
		final Index<Vertex> searchIndex = searchIndices.get(entity.getType().getName());
		final Set<String> indexKeywords = new HashSet<>();
		final DeleteVisitor<VREF> visitor = new DeleteVisitor<>(this, WORD_PATTERN, indexKeywords);
		
		// delete containments and collect index
		for (IPropertyValue<?,VREF> value : entity.getValues())
			value.doWithValue(visitor);
		
		// remove vertex index
		for (String keyword : indexKeywords)
			searchIndex.remove(SEARCH_INDEX, keyword, vertex);
		
		// remove all edges
		//for (Edge edge : vertex.getEdges(Direction.BOTH))
		//	edge.remove();
		
		// remove vertex
		vertex.remove();
	}
	
	private void notifyObservers() {
		for (IObserver observer : new LinkedList<IObserver>(observers))
			observer.update();
	}
	
	@Override
	public Set<V> list(final EntityType type) {
		final LinkedHashSet<V> result = new LinkedHashSet<>();
		final Iterable<Vertex> vertices = type == EntityType.ALL
				? graph.getVertices()
				: graph.getVertices(TYPE_FIELD, type.getName());
		
		for (Vertex vertex : vertices)
			result.add(createEntity(vertex));
		
		return result;
	}
	
	@Override
	public Set<V> list(final EntityType type, final String search) {
		if (search == null || search.isEmpty())
			return list(type);
		
		final LinkedHashSet<V> result = new LinkedHashSet<>();
		final Collection<Index<Vertex>> useIndices;
		final Iterable<String> searchKeywords = createSearchKeywords(search);
		
		if (type == EntityType.ALL) {
			useIndices = searchIndices.values();
		} else {
			useIndices = new LinkedList<>();
			useIndices.add(searchIndices.get(type.getName()));
		}
		
		LinkedHashSet<Vertex> foundVertices = null;
		
		for (String keyword : searchKeywords) {
			final LinkedHashSet<Vertex> keywordVertices = new LinkedHashSet<>();
			
			for (Index<Vertex> searchIndex : useIndices) {
				final CloseableIterable<Vertex> hits = searchIndex.get(SEARCH_INDEX, keyword);
				
				try {
					for (Vertex vertex : hits)
						keywordVertices.add(vertex);
				} finally {
					hits.close();
				}
			}
			
			if (foundVertices == null)
				foundVertices = keywordVertices;
			else
				foundVertices.retainAll(keywordVertices);
		}
		
		if (foundVertices != null)
			for (Vertex vertex : foundVertices)
				result.add(createEntity(vertex));
		
		return result;
	}
	
	private Iterable<String> createSearchKeywords(final String searchPhrase) {
		final LinkedList<String> result = new LinkedList<>();
		final String searchPhraseLowerCase = searchPhrase.toLowerCase();
		final Matcher matcher = WORD_PATTERN.matcher(searchPhraseLowerCase);
		
		while (matcher.find())
			result.add(matcher.group());
		
		return result;
	}

	@Override
	public void addObserver(final IObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(final IObserver observer) {
		observers.remove(observer);
	}

	@Override
	public V find(final IEntityReference entityRef) {
		return createEntity(findVertex(entityRef));
	}
	
	@Override
	public Vertex findVertex(final IEntityReference entityRef) {
		final Iterator<Vertex> iter = graph.getVertices(ID, entityRef.getId()).iterator();
		
		if (!iter.hasNext())
			throw new IllegalArgumentException("vertex with id " + entityRef.getId() + " does not exist");
		
		return iter.next();
	}

	@Override
	public boolean exists(final IEntityReference entityRef) {
		try {
			findVertex(entityRef);
			return true;
		} catch(IllegalArgumentException e) {
			return false;
		}
	}
}
