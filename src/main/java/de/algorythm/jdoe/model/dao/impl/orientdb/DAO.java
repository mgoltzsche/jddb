package de.algorythm.jdoe.model.dao.impl.orientdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.Schema;
import de.algorythm.jdoe.model.meta.visitor.IPropertyValueVisitor;

@Singleton
public class DAO implements IDAO {
	
	static private final Logger log = LoggerFactory.getLogger(DAO.class);
	
	private final Yaml yaml = new Yaml();
	private Schema schema;
	private OrientGraph graph;
	
	private final class SaveVisitor implements IPropertyValueVisitor {
		
		private final Vertex vertex;
		
		public SaveVisitor(final Vertex vertex) {
			this.vertex = vertex;
		}
		
		@Override
		public void doWithEntityCollection(IPropertyValue propertyValue,
				Collection<IEntity> values) {
			final String propertyName = propertyValue.getProperty().getName();
			
			removeOutgoingEdges(propertyName);
			
			for (IEntity entity : values) {
				final Vertex referencedVertex = ((Entity) entity).getVertex();
				
				vertex.addEdge(propertyName, referencedVertex);
			}
		}
		
		@Override
		public void doWithEntity(IPropertyValue propertyValue, IEntity value) {
			final String propertyName = propertyValue.getProperty().getName();
			
			removeOutgoingEdges(propertyName);
			
			if (value != null) {
				final Vertex referencedVertex = ((Entity) value).getVertex();
				
				vertex.addEdge(propertyName, referencedVertex);
			}
		}
		
		@Override
		public void doWithBoolean(IPropertyValue propertyValue, boolean value) {
			writeAttributeValue(propertyValue);
		}

		@Override
		public void doWithDecimal(IPropertyValue propertyValue, Long value) {
			writeAttributeValue(propertyValue);
		}

		@Override
		public void doWithReal(IPropertyValue propertyValue, Double value) {
			writeAttributeValue(propertyValue);
		}

		@Override
		public void doWithDate(IPropertyValue propertyValue, Date value) {
			writeAttributeValue(propertyValue);
		}

		@Override
		public void doWithString(IPropertyValue propertyValue, String value) {
			writeAttributeValue(propertyValue);
		}

		@Override
		public void doWithText(IPropertyValue propertyValue, String value) {
			writeAttributeValue(propertyValue);
		}
		
		private void writeAttributeValue(final IPropertyValue propertyValue) {
			final String propertyName = propertyValue.getProperty().getName();
			final Object value = propertyValue.getValue();
			
			if (value == null)
				vertex.removeProperty(propertyName);
			else
				vertex.setProperty(propertyName, value);
		}
		
		private void removeOutgoingEdges(String propertyName) {
			for (Edge edge : vertex.getEdges(Direction.OUT, propertyName))
				edge.remove();
		}
	};
	
	private final IPropertyValueVisitor deleteVisitor = new IPropertyValueVisitor() {
		
		@Override
		public void doWithEntityCollection(IPropertyValue propertyValue,
				Collection<IEntity> values) {
			if (propertyValue.getProperty().isContainment())
				for (IEntity entity : values)
					delete(entity);
		}
		
		@Override
		public void doWithEntity(IPropertyValue propertyValue, IEntity value) {
			if (propertyValue.getProperty().isContainment())
				delete(value);
		}

		@Override
		public void doWithBoolean(IPropertyValue propertyValue, boolean value) {
		}

		@Override
		public void doWithDecimal(IPropertyValue propertyValue, Long value) {
		}

		@Override
		public void doWithReal(IPropertyValue propertyValue, Double value) {
		}

		@Override
		public void doWithDate(IPropertyValue propertyValue, Date value) {
		}

		@Override
		public void doWithString(IPropertyValue propertyValue, String value) {
		}

		@Override
		public void doWithText(IPropertyValue propertyValue, String value) {
		}
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
	public void setSchema(Schema schema) throws IOException {
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
	public IEntity create(final EntityType type) {
		final Vertex vertex = graph.addVertex(UUID.randomUUID().toString());
		vertex.setProperty(Entity.TYPE_FIELD, type.getName());
		
		return new Entity(schema, vertex);
	}
	
	@Override
	public void save(IEntity entity) {
		final Vertex vertex = ((Entity) entity).getVertex();
		final IPropertyValueVisitor visitor = new SaveVisitor(vertex);
		
		for (IPropertyValue propertyValue : entity.getValues())
			propertyValue.getProperty().doWithPropertyValue(propertyValue, visitor);
	}

	@Override
	public void delete(IEntity entity) {
		final Entity entityImpl = (Entity) entity;
		
		for (IPropertyValue value : entity.getValues())
			value.getProperty().doWithPropertyValue(value, deleteVisitor);
		
		entityImpl.getVertex().remove();
	}

	@Override
	public Collection<IEntity> list(final EntityType type) {
		final LinkedList<IEntity> result = new LinkedList<>();
		
		for (Vertex vertex : graph.getVertices(Entity.TYPE_FIELD, type.getName()))
			result.add(new Entity(schema, vertex));
		
		System.out.println("listing vertices: " + result.size());
		
		return result;
	}
}
