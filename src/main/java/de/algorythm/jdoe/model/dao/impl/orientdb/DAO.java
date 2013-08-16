package de.algorythm.jdoe.model.dao.impl.orientdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.yaml.snakeyaml.Yaml;

import com.tinkerpop.blueprints.CloseableIterable;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.dao.IObserver;
import de.algorythm.jdoe.model.dao.impl.orientdb.visitor.DeleteVisitor;
import de.algorythm.jdoe.model.dao.impl.orientdb.visitor.IndexKeywordCollectingVisitor;
import de.algorythm.jdoe.model.dao.impl.orientdb.visitor.SaveVisitor;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.impl.AbstractPropertyValue;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.Schema;

@Singleton
public class DAO implements IDAO, IDAOVisitorContext {
	
	static private final String SEARCH_INDEX = "searchIndex";
	static private final Pattern WORD_PATTERN = Pattern.compile("[\\w]+");
	
	private final Yaml yaml = new Yaml();
	private Schema schema;
	private OrientGraph graph;
	private final HashSet<IObserver> observers = new HashSet<>();
	private final HashMap<String, Index<Vertex>> searchIndices = new HashMap<>();
	
	public void open() throws IOException {
		loadSchema();
		
		graph = new OrientGraph("local:graph.db");
		
		graph.setUseLightweightEdges(true);
		graph.createKeyIndex(Entity.TYPE_FIELD, Vertex.class);
		
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
		// update property indices
		for (EntityType type : schema.getTypes()) {
			int i = 0;
			
			for (Property property : type.getProperties()) {
				((Property) property).setIndex(i);
				i++;
			}
		}
		
		// save schema
		yaml.dump(schema, new FileWriter(new File("schema.yaml")));
		this.schema = schema;
	}

	@Override
	public IEntity createEntity(final EntityType type) {
		return new Entity(schema, type);
	}
	
	@Override
	public IEntity createEntity(final Vertex vertex) {
		return new Entity(schema, vertex);
	}
	
	@Override
	public void save(final IEntity entity) {
		if (!entity.isChanged())
			return;
		
		final LinkedList<Entity> savedEntities = new LinkedList<>();
		
		try {
			saveInTransaction(entity, savedEntities);
			graph.commit();
		} catch(Throwable e) {
			graph.rollback();
			
			for (Entity savedEntity : savedEntities) {
				if (savedEntity.isPersisted())
					savedEntity.update();
				else
					savedEntity.setVertex(null);
			}
			
			throw e;
		}
		
		// set entity unchanged
		for (Entity savedEntity : savedEntities) {
			savedEntity.setPersisted(true);
			
			for (IPropertyValue<?> propertyValue : savedEntity.getValues()) {
				final AbstractPropertyValue<?> propertyValueImpl = (AbstractPropertyValue<?>) propertyValue;
				
				propertyValueImpl.setChanged(false);
			}
		}
		
		notifyObservers();
	}
	
	@Override
	public void saveInTransaction(final IEntity entity, final Collection<Entity> savedEntities) {
		final Entity entityImpl = (Entity) entity;
		Vertex vertex = entityImpl.getVertex();
		final Index<Vertex> searchIndex = searchIndices.get(entity.getType().getName());
		
		savedEntities.add(entityImpl);
		
		if (vertex == null) { // create new vertex
			vertex = graph.addVertex(null);
			vertex.setProperty(Entity.ID, entity.getId());
			vertex.setProperty(Entity.TYPE_FIELD, entity.getType().getName());
			entityImpl.setVertex(vertex);
		} else { // remove old vertex index
			for (String keyword : createIndexKeywords(createEntity(vertex)))
				searchIndex.remove(SEARCH_INDEX, keyword, vertex);
		}
		
		final Set<String> indexKeywords = new HashSet<>();
		final SaveVisitor visitor = new SaveVisitor(this, vertex, savedEntities, WORD_PATTERN, indexKeywords);
		
		// assign values to vertex
		for (IPropertyValue<?> propertyValue : entity.getValues())
			propertyValue.doWithValue(visitor);
		System.out.println("## " + indexKeywords);
		// rebuild vertex index
		for (String keyword : indexKeywords)
			searchIndex.put(SEARCH_INDEX, keyword, vertex);
	}
	
	private Iterable<String> createIndexKeywords(final IEntity entity) {
		final Set<String> indexKeywords = new HashSet<>();
		final IndexKeywordCollectingVisitor visitor = new IndexKeywordCollectingVisitor(WORD_PATTERN, indexKeywords);
		
		for (IPropertyValue<?> propertyValue : entity.getValues())
			propertyValue.doWithValue(visitor);
		
		return indexKeywords;
	}
	
	@Override
	public void delete(final IEntity entity) {
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
	public void deleteInTransaction(final IEntity entity) {
		final Entity entityImpl = (Entity) entity;
		final Vertex vertex = entityImpl.getVertex();
		final Index<Vertex> searchIndex = searchIndices.get(entity.getType().getName());
		final Set<String> indexKeywords = new HashSet<>();
		final DeleteVisitor visitor = new DeleteVisitor(this, WORD_PATTERN, indexKeywords);
		
		// delete containments and collect index
		for (IPropertyValue<?> value : entity.getValues())
			value.doWithValue(visitor);
		
		// remove vertex index
		for (String keyword : indexKeywords)
			searchIndex.remove(SEARCH_INDEX, keyword, vertex);
		
		vertex.remove();
	}

	private void notifyObservers() {
		for (IObserver observer : new LinkedList<IObserver>(observers))
			observer.update();
	}
	
	@Override
	public Set<IEntity> list(final EntityType type) {
		final LinkedHashSet<IEntity> result = new LinkedHashSet<>();
		final Iterable<Vertex> vertices = type == EntityType.ALL
				? graph.getVertices()
				: graph.getVertices(Entity.TYPE_FIELD, type.getName());
		
		for (Vertex vertex : vertices)
			result.add(createEntity(vertex));
		
		return result;
	}
	
	@Override
	public Set<IEntity> list(final EntityType type, final String search) {
		if (search == null || search.isEmpty())
			return list(type);
		
		final LinkedHashSet<IEntity> result = new LinkedHashSet<>();
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
	public boolean update(final IEntity entity) {
		final Entity entityImpl = (Entity) entity;
		final Vertex vertex = entityImpl.getVertex();
		
		if (vertex == null)
			throw new IllegalArgumentException("entity is not persistent");
		
		final boolean notExists = graph.getVertex(vertex.getId()) == null;
		
		if (notExists)
			return false;
		
		entityImpl.update();
		
		return true;
	}
	
	@Override
	public boolean exists(final IEntity entity) {
		final Entity entityImpl = (Entity) entity;
		final Vertex vertex = entityImpl.getVertex();
		
		if (vertex == null)
			throw new IllegalArgumentException("entity is not persistent");
		
		return graph.getVertex(vertex.getId()) != null;
	}

	@Override
	public void addObserver(final IObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(final IObserver observer) {
		observers.remove(observer);
	}
}
