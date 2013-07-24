package de.algorythm.jdoe.model.dao.impl.orientdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.Schema;
import de.algorythm.jdoe.model.meta.visitor.IPropertyValueVisitor;

public class Entity implements IEntity, IPropertyValueVisitor {

	static private final long serialVersionUID = -4116231309999192319L;
	static final String TYPE_FIELD = "_type";
	
	private Schema schema;
	private EntityType type;
	private Vertex vertex;
	private ArrayList<IPropertyValue> values;
	
	public Entity(final Schema schema, final EntityType type) {
		this.schema = schema;
		this.type = type;
	}
	
	public Entity(final Schema schema, final Vertex vertex) {
		this(schema, (EntityType) null);
		
		this.vertex = vertex;
		type = getTypeByVertex(vertex);
	}
	
	public Vertex getVertex() {
		return vertex;
	}
	
	@Override
	public EntityType getType() {
		return type;
	}

	@Override
	public Iterable<IPropertyValue> getValues() {
		lazyLoadValues();
		return values;
	}
	
	protected void lazyLoadValues() {
		if (values == null) {
			values = new ArrayList<>(type.getProperties().size());
			
			if (vertex != null) {
				for (Property property : type.getProperties()) {
					final PropertyValue value = new PropertyValue(property);
					
					property.doWithPropertyValue(value, this);
					value.setChanged(false);
					
					values.add(value);
				}
			}
		}
	}
	
	@Override
	public IPropertyValue getValue(int index) {
		lazyLoadValues();
		
		return values.get(index);
	}
	
	@Override
	public Iterable<IEntity> getReferencingEntities() {
		final LinkedList<IEntity> referencingEntities = new LinkedList<>();
		
		if (vertex != null)
			for (Vertex referencingVertex : vertex.getVertices(Direction.IN))
				referencingEntities.add(new Entity(schema, referencingVertex));
		
		return referencingEntities;
	}
	
	private EntityType getTypeByVertex(final Vertex vertex) {
		final String typeName = vertex.<String>getProperty(TYPE_FIELD);
		
		return schema.getTypeByName(typeName);
	}

	@Override
	public void doWithBoolean(IPropertyValue propertyValue, boolean value) {
		readAttributeValue(propertyValue);
	}

	@Override
	public void doWithDecimal(IPropertyValue propertyValue, Long value) {
		readAttributeValue(propertyValue);
	}

	@Override
	public void doWithReal(IPropertyValue propertyValue, Double value) {
		readAttributeValue(propertyValue);
	}

	@Override
	public void doWithDate(IPropertyValue propertyValue, Date value) {
		readAttributeValue(propertyValue);
	}

	@Override
	public void doWithString(IPropertyValue propertyValue, String value) {
		readAttributeValue(propertyValue);
	}

	@Override
	public void doWithText(IPropertyValue propertyValue, String value) {
		readAttributeValue(propertyValue);
	}
	
	private void readAttributeValue(IPropertyValue propertyValue) {
		propertyValue.setValue(vertex.getProperty(propertyValue.getProperty().getName()));
	}
	
	@Override
	public void doWithEntity(IPropertyValue propertyValue, IEntity value) {
		final String propertyName = propertyValue.getProperty().getName();
		
		for (Vertex referencingVertex : vertex.getVertices(Direction.OUT, propertyName)) {
			propertyValue.setValue(new Entity(schema, referencingVertex));
			return;
		}
	}

	@Override
	public void doWithEntityCollection(IPropertyValue propertyValue,
			Collection<IEntity> values) {
		final LinkedList<IEntity> associations = new LinkedList<>();
		final String propertyName = propertyValue.getProperty().getName();
		
		for (Vertex referencingVertex : vertex.getVertices(Direction.OUT, propertyName))
			associations.add(new Entity(schema, referencingVertex));
		
		propertyValue.setValue(associations);
	}
	
	@Override
	public String toString() {
		final String csv = values.toString();
		return csv.substring(1, csv.length() - 1);
	}
}
