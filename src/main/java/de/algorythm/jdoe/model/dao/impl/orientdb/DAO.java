package de.algorythm.jdoe.model.dao.impl.orientdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
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
	
	static private final Logger log = LoggerFactory.getLogger(DAO.class);
	
	private final Yaml yaml = new Yaml();
	private Schema schema;
	private OrientGraph graph;
	private final HashSet<IObserver> observers = new HashSet<>();
	
	private final class SaveVisitor implements IPropertyValueVisitor {
		
		private final Vertex vertex;
		
		public SaveVisitor(final Vertex vertex) {
			this.vertex = vertex;
		}
		
		@Override
		public void doWithAssociations(Associations propertyValue) {
			final Property property = propertyValue.getProperty();
			final String propertyName = property.getName();
			final LinkedList<Edge> existingEdges = new LinkedList<Edge>();
			
			for (Edge edge : vertex.getEdges(Direction.OUT, propertyName))
				existingEdges.add(edge);
			
			for (IEntity refEntity : propertyValue.getValue()) {
				if (refEntity.getId() == null) // save new entity
					saveInTransaction(refEntity);
				
				// check existing edge
				boolean edgeAlreadyExists = false;
				
				final Iterator<Edge> existingEdgeIter = existingEdges.iterator();
				
				while (existingEdgeIter.hasNext()) {
					final Edge edge = existingEdgeIter.next();
					
					if (edge.getVertex(Direction.IN).getId().equals(refEntity.getId())) {
						edgeAlreadyExists = true;
						existingEdgeIter.remove();
						break;
					}
				}
				
				if (!edgeAlreadyExists) // save new edge
					vertex.addEdge(propertyName, ((Entity) refEntity).getVertex());
			}
			
			// remove invalid edges
			for (Edge edge : existingEdges)
				deleteEdge(property, edge);
		}
		
		@Override
		public void doWithAssociation(Association propertyValue) {
			final Property property = propertyValue.getProperty();
			final String propertyName = property.getName();
			final IEntity value = propertyValue.getValue();
			
			if (value != null && value.getId() == null)
				saveInTransaction(value);
			
			boolean edgeAlreadyExists = false;
			
			// remove outgoing edges
			for (Edge edge : vertex.getEdges(Direction.OUT, propertyName)) {
				if (value == null)
					deleteEdge(property, edge);
				else if (edge.getVertex(Direction.IN).getId().equals(value.getId()))
					edgeAlreadyExists = true;
				else
					deleteEdge(property, edge);
			}
			
			if (value != null && !edgeAlreadyExists) // add edge if not exists
				vertex.addEdge(propertyName, ((Entity) value).getVertex());
		}
		
		@Override
		public void doWithBoolean(BooleanValue propertyValue) {
			writeAttributeValue(propertyValue.getProperty(), propertyValue.getValue());
		}

		@Override
		public void doWithDecimal(DecimalValue propertyValue) {
			writeAttributeValue(propertyValue.getProperty(), propertyValue.getValue());
		}

		@Override
		public void doWithReal(RealValue propertyValue) {
			writeAttributeValue(propertyValue.getProperty(), propertyValue.getValue());
		}

		@Override
		public void doWithDate(DateValue propertyValue) {
			writeAttributeValue(propertyValue.getProperty(), propertyValue.getValue());
		}

		@Override
		public void doWithString(StringValue propertyValue) {
			writeAttributeValue(propertyValue.getProperty(), propertyValue.getValue());
		}

		@Override
		public void doWithText(TextValue propertyValue) {
			writeAttributeValue(propertyValue.getProperty(), propertyValue.getValue());
		}
		
		private void writeAttributeValue(final Property property, final Object value) {
			final String propertyName = property.getName();
			
			if (value == null)
				vertex.removeProperty(propertyName);
			else
				vertex.setProperty(propertyName, value);
		}
		
		private void deleteEdge(final Property property, final Edge edge) {
			final Vertex referredVertex = edge.getVertex(Direction.IN);
			
			edge.remove();
			
			if (property.isContainment()) {
				final Entity referredEntity = new Entity(schema, referredVertex);
				
				if (property.getType().isConform(referredEntity.getType()))
					deleteInTransaction(referredEntity);
			}
		}
	};
	
	private final IPropertyValueVisitor deleteVisitor = new IPropertyValueVisitor() {
		
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
		public void doWithBoolean(BooleanValue propertyValue) {}

		@Override
		public void doWithDecimal(DecimalValue propertyValue) {}

		@Override
		public void doWithReal(RealValue propertyValue) {}

		@Override
		public void doWithDate(DateValue propertyValue) {}

		@Override
		public void doWithString(StringValue propertyValue) {}

		@Override
		public void doWithText(TextValue propertyValue) {}
	};
	
	public void open() throws IOException {
		loadSchema();
		
		graph = new OrientGraph("local:graph.db");
		
		System.out.println("db opened");
	}
	
	public void close() {
		graph.shutdown();
		System.out.println("db closed");
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
	public synchronized void save(final IEntity entity) {
		try {
			saveInTransaction(entity);
			graph.commit();
		} catch(Throwable e) {
			graph.rollback();
			throw e;
		}
		
		notifyObservers();
	}
	
	private void saveInTransaction(final IEntity entity) {
		final Entity entityImpl = (Entity) entity;
		Vertex vertex = entityImpl.getVertex();
		
		if (vertex == null) {
			vertex = graph.addVertex(null);
			vertex.setProperty(Entity.TYPE_FIELD, entity.getType().getName());
			entityImpl.setVertex(vertex);
		}
		
		final IPropertyValueVisitor visitor = new SaveVisitor(vertex);
		
		for (IPropertyValue propertyValue : entity.getValues())
			propertyValue.doWithValue(visitor);
	}

	@Override
	public synchronized void delete(final IEntity entity) {
		try {
			deleteInTransaction(entity);
			graph.commit();
		} catch(Throwable e) {
			graph.rollback();
		}
		
		notifyObservers();
	}
	
	private void deleteInTransaction(final IEntity entity) {
		final Entity entityImpl = (Entity) entity;
		
		for (IPropertyValue value : entity.getValues())
			value.doWithValue(deleteVisitor);
		
		entityImpl.getVertex().remove();
	}

	private void notifyObservers() {
		for (IObserver observer : observers)
			observer.update();
	}
	
	@Override
	public Collection<IEntity> list(final EntityType type) {
		final LinkedList<IEntity> result = new LinkedList<>();
		
		for (Vertex vertex : graph.getVertices(Entity.TYPE_FIELD, type.getName()))
			result.add(new Entity(schema, vertex));
		
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
}
