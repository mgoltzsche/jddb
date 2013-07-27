package de.algorythm.jdoe.model.dao.impl.orientdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.entity.impl.AbstractPropertyValue;
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

public class Entity implements IEntity, IPropertyValueVisitor {

	static private final long serialVersionUID = -4116231309999192319L;
	static final String TYPE_FIELD = "_type";
	
	private Schema schema;
	private EntityType type;
	private Vertex vertex;
	private ArrayList<IPropertyValue> values;
	private String id;
	
	public Entity(final Schema schema, final EntityType type) {
		this.schema = schema;
		this.type = type;
	}
	
	public Entity(final Schema schema, final Vertex vertex) {
		this.schema = schema;
		this.vertex = vertex;
		id = vertex.getId().toString();
		type = getTypeByVertex(vertex);
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	public void setId(final String id) {
		this.id = id;
	}
	
	@Override
	public boolean isPersisted() {
		return vertex != null;
	}
	
	public Vertex getVertex() {
		return vertex;
	}
	
	public void setVertex(final Vertex vertex) {
		this.vertex = vertex;
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
			
			for (Property property : type.getProperties()) {
				final IPropertyValue propertyValue = property.createPropertyValue();
				
				if (vertex != null) {
					propertyValue.doWithValue(this);
					((AbstractPropertyValue) propertyValue).setChanged(false);
				}
				
				values.add(propertyValue);
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
	public void doWithBoolean(final BooleanValue propertyValue) {
		final String valueAsString = attributeValueAsString(propertyValue);
		
		if (valueAsString != null)
			propertyValue.setValue(Boolean.valueOf(valueAsString));
	}
	
	@Override
	public void doWithDecimal(final DecimalValue propertyValue) {
		final String valueAsString = attributeValueAsString(propertyValue);
		
		if (valueAsString != null)
			propertyValue.setValue(Long.valueOf(valueAsString));
	}

	@Override
	public void doWithReal(final RealValue propertyValue) {
		final String valueAsString = attributeValueAsString(propertyValue);
		
		if (valueAsString != null)
			propertyValue.setValue(Double.valueOf(valueAsString));
	}

	@Override
	public void doWithDate(final DateValue propertyValue) {
		final String valueAsString = attributeValueAsString(propertyValue);
		
		if (valueAsString != null) {
			final long timestamp = Long.valueOf(valueAsString);
			final Date value = new Date(timestamp);
			propertyValue.setValue(value);
		}
	}

	@Override
	public void doWithString(StringValue propertyValue) {
		propertyValue.setValue(attributeValueAsString(propertyValue));
	}

	@Override
	public void doWithText(TextValue propertyValue) {
		propertyValue.setValue(attributeValueAsString(propertyValue));
	}
	
	private String attributeValueAsString(final IPropertyValue propertyValue) {
		final Object value = vertex.getProperty(propertyValue.getProperty().getName());
		
		if (value == null)
			return null;
		else {
			final String valueStr = value.toString();
			
			return valueStr.isEmpty() ? null : valueStr;
		}
	}
	
	@Override
	public void doWithAssociation(Association propertyValue) {
		final String propertyName = propertyValue.getProperty().getName();
		
		for (Vertex referencingVertex : vertex.getVertices(Direction.OUT, propertyName)) {
			propertyValue.setValue(new Entity(schema, referencingVertex));
			return;
		}
	}

	@Override
	public void doWithAssociations(Associations propertyValue) {
		final HashSet<IEntity> associations = new HashSet<>();
		final String propertyName = propertyValue.getProperty().getName();
		
		for (Vertex referencingVertex : vertex.getVertices(Direction.OUT, propertyName))
			associations.add(new Entity(schema, referencingVertex));
		
		propertyValue.setValue(associations);
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		for (IPropertyValue value : getValues()) {
			final String valueStr = value.toString();
			
			if (valueStr != null) {
				if (sb.length() > 0)
					sb.append(", ");
				
				sb.append(valueStr);
			}
		}
		
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return 31 * 1 + id.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		final Entity other = (Entity) obj;
		
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		
		return true;
	}
}
