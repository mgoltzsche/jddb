package de.algorythm.jdoe.model.dao.impl.orientdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

import org.yaml.snakeyaml.Yaml;

import com.tinkerpop.blueprints.CloseableIterable;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.dao.IObserver;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.entity.impl.Association;
import de.algorythm.jdoe.model.entity.impl.Associations;
import de.algorythm.jdoe.model.entity.impl.BooleanValue;
import de.algorythm.jdoe.model.entity.impl.DateValue;
import de.algorythm.jdoe.model.entity.impl.DecimalValue;
import de.algorythm.jdoe.model.entity.impl.RealValue;
import de.algorythm.jdoe.model.entity.impl.StringValue;
import de.algorythm.jdoe.model.entity.impl.TextValue;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.Schema;

@Singleton
public class DAO implements IDAO {
	
	static private final String SEARCH_INDEX = "searchIndex";
	static private final Pattern WORD_PATTERN = Pattern.compile("[\\w]+");
	
	private final Yaml yaml = new Yaml();
	private Schema schema;
	private OrientGraph graph;
	private final HashSet<IObserver> observers = new HashSet<>();
	private final HashMap<String, Index<Vertex>> searchIndices = new HashMap<>();
	
	private final class SaveVisitor implements IPropertyValueVisitor {
		
		private final Vertex vertex;
		private final Index<Vertex> searchIndex;
		
		public SaveVisitor(final Entity entity) {
			vertex = entity.getVertex();
			searchIndex = searchIndices.get(entity.getType().getName());
		}
		
		@Override
		public void doWithAssociations(Associations propertyValue) {
			final Property property = propertyValue.getProperty();
			final String propertyName = property.getName();
			final LinkedList<Edge> existingEdges = new LinkedList<Edge>();
			
			for (Edge edge : vertex.getEdges(Direction.OUT, propertyName))
				existingEdges.add(edge);
			
			for (IEntity refEntity : propertyValue.getValue()) {
				if (!refEntity.isPersisted()) // save new entity
					saveInTransaction(refEntity);
				
				// check existing edge
				boolean edgeAlreadyExists = false;
				
				final Vertex refVertex = ((Entity) refEntity).getVertex();
				final Iterator<Edge> existingEdgeIter = existingEdges.iterator();
				
				while (existingEdgeIter.hasNext()) {
					final Edge edge = existingEdgeIter.next();
					
					if (edge.getVertex(Direction.IN).equals(refVertex)) {
						edgeAlreadyExists = true;
						existingEdgeIter.remove();
						break;
					}
				}
				
				if (!edgeAlreadyExists) // save new edge
					vertex.addEdge(propertyName, refVertex);
			}
			
			// remove invalid edges
			for (Edge edge : existingEdges)
				deleteEdge(property, edge);
		}
		
		@Override
		public void doWithAssociation(Association propertyValue) {
			final Property property = propertyValue.getProperty();
			final String propertyName = property.getName();
			final Entity refEntity = (Entity) propertyValue.getValue();
			Vertex refVertex = null;
			
			if (refEntity != null) {
				if (!refEntity.isPersisted())
					saveInTransaction(refEntity);
				
				refVertex = refEntity.getVertex();
			}
			
			boolean edgeAlreadyExists = false;
			
			// remove outgoing edges
			for (Edge edge : vertex.getEdges(Direction.OUT, propertyName)) {
				if (refVertex == null)
					deleteEdge(property, edge);
				else if (edge.getVertex(Direction.IN).equals(refVertex))
					edgeAlreadyExists = true;
				else
					deleteEdge(property, edge);
			}
			
			if (refVertex != null && !edgeAlreadyExists) // add edge if not exists
				vertex.addEdge(propertyName, refVertex);
		}
		
		@Override
		public void doWithBoolean(BooleanValue propertyValue) {
			writeAttributeValue(propertyValue);
		}

		@Override
		public void doWithDecimal(DecimalValue propertyValue) {
			writeAttributeValue(propertyValue);
		}

		@Override
		public void doWithReal(RealValue propertyValue) {
			writeAttributeValue(propertyValue);
		}

		@Override
		public void doWithDate(DateValue propertyValue) {
			writeAttributeValue(propertyValue);
		}

		@Override
		public void doWithString(StringValue propertyValue) {
			writeAttributeValue(propertyValue);
		}

		@Override
		public void doWithText(TextValue propertyValue) {
			writeAttributeValue(propertyValue);
		}
		
		private void writeAttributeValue(IPropertyValue<?> propertyValue) {
			final Property property = propertyValue.getProperty();
			final String propertyName = property.getName();
			final Object newValue = propertyValue.getValue();
			final Object oldValue = vertex.getProperty(propertyName);
			
			if (newValue == null && oldValue == null ||
					newValue != null && newValue.equals(oldValue))
				return; // do nothing if value hasn't changed
			
			// remove existing indices for value
			if (oldValue != null && property.isSearchable())
				for (String oldKeyword : createLeftTruncatedIndexKeywords(oldValue))
					searchIndex.remove(SEARCH_INDEX, oldKeyword, vertex);
			
			System.out.println("new value: " + newValue);
			// persist new value
			if (newValue == null) {
				vertex.removeProperty(propertyName);
			} else {
				vertex.setProperty(propertyName, newValue);
				
				if (property.isSearchable())
					for (String keyword : createLeftTruncatedIndexKeywords(newValue))
						searchIndex.put(SEARCH_INDEX, keyword, vertex);
			}
		}
		
		private void deleteEdge(final Property property, final Edge edge) {
			System.out.println("delete edge " + property.getLabel() + ", containment: " + property.isContainment());
			final Vertex referredVertex = edge.getVertex(Direction.IN);
			
			edge.remove();
			
			if (property.isContainment()) {
				final Entity referredEntity = new Entity(schema, referredVertex);
				
				if (property.getType().isConform(referredEntity.getType()))
					deleteInTransaction(referredEntity);
			}
		}
	};
	
	private final class DeleteVisitor implements IPropertyValueVisitor {
		
		private final Vertex vertex;
		private final Index<Vertex> searchIndex;
		
		public DeleteVisitor(final Entity entity) {
			vertex = entity.getVertex();
			searchIndex = searchIndices.get(entity.getType().getName());
		}
		
		@Override
		public void doWithAssociations(Associations propertyValue) {
			if (propertyValue.getProperty().isContainment())
				for (IEntity entity : propertyValue.getValue())
					deleteInTransaction(entity);
		}
		
		@Override
		public void doWithAssociation(Association propertyValue) {
			if (propertyValue.getProperty().isContainment()) {
				final IEntity entity = propertyValue.getValue();
				
				if (entity != null)
					deleteInTransaction(entity);
			}
		}

		@Override
		public void doWithBoolean(BooleanValue propertyValue) {
			removeIndex(propertyValue);
		}

		@Override
		public void doWithDecimal(DecimalValue propertyValue) {
			removeIndex(propertyValue);
		}

		@Override
		public void doWithReal(RealValue propertyValue) {
			removeIndex(propertyValue);
		}

		@Override
		public void doWithDate(DateValue propertyValue) {
			removeIndex(propertyValue);
		}

		@Override
		public void doWithString(StringValue propertyValue) {
			removeIndex(propertyValue);
		}

		@Override
		public void doWithText(TextValue propertyValue) {
			removeIndex(propertyValue);
		}
		
		private void removeIndex(final IPropertyValue<?> propertyValue) {
			final String propertyName = propertyValue.getProperty().getName();
			final Object value = vertex.getProperty(propertyName);
			
			if (value != null)
				for (String keyword : createLeftTruncatedIndexKeywords(value))
					searchIndex.remove(SEARCH_INDEX, keyword, vertex);
		}
	};
	
	private Iterable<String> createLeftTruncatedIndexKeywords(final Object value) {
		final LinkedList<String> keywords = new LinkedList<>();
		
		if (value != null) {
			final String valueStr = value.toString().toLowerCase();
			final Matcher matcher = WORD_PATTERN.matcher(valueStr);
			
			while (matcher.find()) {
				final String foundWord = matcher.group();
				
				for (int i = 1; i <= foundWord.length(); i++) {
					final String truncatedWord = foundWord.substring(0, i);
					
					keywords.add(truncatedWord);
				}
			}
		}
		
		return keywords;
	}
	
	private Iterable<String> createIndexKeywords(final String value) {
		final LinkedList<String> keywords = new LinkedList<>();
		
		if (value != null) {
			final Matcher matcher = WORD_PATTERN.matcher(value.toLowerCase());
			
			while (matcher.find())
				keywords.add(matcher.group());
		}
		
		return keywords;
	}
	
	public void open() throws IOException {
		synchronized(this) {
			loadSchema();
			
			graph = new OrientGraph("local:graph.db");
			
			graph.setUseLightweightEdges(true);
			graph.createKeyIndex(Entity.TYPE_FIELD, Vertex.class);
			
			createIndices();
			
			System.out.println("db opened");
		}
	}
	
	private void createIndices() {
		for (EntityType type : schema.getTypes()) {
			final String typeName = type.getName();
			Index<Vertex> typeIndex = graph.getIndex(typeName, Vertex.class);
			
			if (typeIndex == null)
				typeIndex = graph.createIndex(typeName, Vertex.class);
			
			searchIndices.put(typeName, typeIndex);
		}
	}
	
	public void close() {
		synchronized(this) {
			graph.shutdown();
			
			System.out.println("db closed");
		}
	}
	
	private void loadSchema() throws IOException {
		synchronized(this) {
			try {
				schema = yaml.loadAs(new FileReader(new File("schema.yaml")), Schema.class);
			} catch(FileNotFoundException e) {
				schema = new Schema();
			}
		}
	}
	
	@Override
	public Schema getSchema() {
		return schema;
	}

	@Override
	public void setSchema(final Schema schema) throws IOException {
		synchronized(this) {
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
	}

	@Override
	public IEntity createEntity(final EntityType type) {
		return new Entity(schema, type);
	}
	
	@Override
	public void save(final IEntity entity) {
		synchronized(this) {
			try {
				saveInTransaction(entity);
				graph.commit();
			} catch(Throwable e) {
				graph.rollback();
				throw e;
			}
		}
		
		notifyObservers();
	}
	
	private void saveInTransaction(final IEntity entity) {
		final Entity entityImpl = (Entity) entity;
		Vertex vertex = entityImpl.getVertex();
		
		if (vertex == null) {
			vertex = graph.addVertex(null);
			vertex.setProperty(Entity.ID, entity.getId());
			vertex.setProperty(Entity.TYPE_FIELD, entity.getType().getName());
			entityImpl.setVertex(vertex);
		}
		
		final IPropertyValueVisitor visitor = new SaveVisitor(entityImpl);
		
		for (IPropertyValue<?> propertyValue : entity.getValues())
			propertyValue.doWithValue(visitor);
	}

	@Override
	public void delete(final IEntity entity) {
		synchronized(this) {
			try {
				deleteInTransaction(entity);
				graph.commit();
			} catch(Throwable e) {
				graph.rollback();
				throw e;
			}
		}
		
		notifyObservers();
	}
	
	private void deleteInTransaction(final IEntity entity) {
		final Entity entityImpl = (Entity) entity;
		final Vertex vertex = entityImpl.getVertex();
		final IPropertyValueVisitor visitor = new DeleteVisitor(entityImpl);
		
		for (IPropertyValue<?> value : entity.getValues())
			value.doWithValue(visitor);
		
		vertex.remove();
	}

	private void notifyObservers() {
		for (IObserver observer : new LinkedList<IObserver>(observers))
			observer.update();
	}
	
	@Override
	public Set<IEntity> list(final EntityType type) {
		synchronized(this) {
			final LinkedHashSet<IEntity> result = new LinkedHashSet<>();
			final Iterable<Vertex> vertices = type == EntityType.ALL
					? graph.getVertices()
					: graph.getVertices(Entity.TYPE_FIELD, type.getName());
			
			for (Vertex vertex : vertices)
				result.add(new Entity(schema, vertex));
			
			return result;
		}
	}
	
	@Override
	public Set<IEntity> list(final EntityType type, final String search) {
		if (search == null || search.isEmpty())
			return list(type);
		
		synchronized(this) {
			final LinkedHashSet<IEntity> result = new LinkedHashSet<>();
			final Collection<Index<Vertex>> useIndices;
			final Iterable<String> searchKeywords = createIndexKeywords(search);
			
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
					result.add(new Entity(schema, vertex));
			
			return result;
		}
	}
	
	@Override
	public boolean update(final IEntity entity) {
		final Entity entityImpl = (Entity) entity;
		final Vertex vertex = entityImpl.getVertex();
		
		if (vertex == null)
			throw new IllegalArgumentException("entity is not persistent");
		
		synchronized(this) {
			final boolean notExists = graph.getVertex(vertex.getId()) == null;
			
			if (notExists)
				return false;
			
			entityImpl.update();
			
			return true;
		}
	}
	
	@Override
	public boolean exists(final IEntity entity) {
		final Entity entityImpl = (Entity) entity;
		final Vertex vertex = entityImpl.getVertex();
		
		if (vertex == null)
			throw new IllegalArgumentException("entity is not persistent");
		
		synchronized(this) {
			return graph.getVertex(vertex.getId()) != null;
		}
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
