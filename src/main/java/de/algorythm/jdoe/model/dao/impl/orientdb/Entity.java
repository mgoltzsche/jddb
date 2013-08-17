package de.algorythm.jdoe.model.dao.impl.orientdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	static private final Logger LOG = LoggerFactory.getLogger(Entity.class);
	
	static private final long serialVersionUID = -4116231309999192319L;
	static final String TYPE_FIELD = "_type";
	static final String ID = "_id";
	
	private transient Schema schema;
	private EntityType type;
	private transient Vertex vertex;
	private ArrayList<IPropertyValue<?>> values;
	private String id;
	private transient boolean persisted;
	
	public Entity(final Schema schema, final EntityType type) {
		this.schema = schema;
		this.type = type;
		id = UUID.randomUUID().toString();
		persisted = false;
		loadProperties();
	}
	
	public Entity(final Schema schema, final Vertex vertex) {
		this.schema = schema;
		setVertex(vertex);
		id = vertex.getProperty(ID);
		type = getTypeByVertex(vertex);
		persisted = true;
		loadProperties();
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public boolean isPersisted() {
		return persisted;
	}
	
	public void setPersisted(final boolean persisted) {
		this.persisted = persisted;
	}
	
	@Override
	public boolean isChanged() {
		for (IPropertyValue<?> value : values)
			if (value.isChanged())
				return true;
		
		return false;
	}
	
	public Vertex getVertex() {
		return vertex;
	}
	
	public void setVertex(final Vertex vertex) {
		this.vertex = vertex;
	}
	
	public void update() {
		for (IPropertyValue<?> propertyValue : values)
			updatePropertyValue(propertyValue);
	}
	
	@Override
	public EntityType getType() {
		return type;
	}
	
	@Override
	public Collection<IPropertyValue<?>> getValues() {
		return values;
	}
	
	private void loadProperties() {
		values = new ArrayList<>(type.getProperties().size());
		
		for (Property property : type.getProperties()) {
			final IPropertyValue<?> propertyValue = property.createPropertyValue();
			
			if (vertex == null)
				((AbstractPropertyValue<?>) propertyValue).setChanged(true);
			else
				updatePropertyValue(propertyValue);
			
			values.add(propertyValue);
		}
	}
	
	private void updatePropertyValue(IPropertyValue<?> propertyValue) {
		propertyValue.doWithValue(this);
		
		((AbstractPropertyValue<?>) propertyValue).setChanged(false);
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
			try {
				propertyValue.setValue(Boolean.valueOf(valueAsString));
			} catch(Throwable e) {
				propertyValue.setValue(false);
				LOG.debug("Couldn't read boolean property " + propertyValue.getProperty().getLabel(), e);
			}
	}
	
	@Override
	public void doWithDecimal(final DecimalValue propertyValue) {
		final String valueAsString = attributeValueAsString(propertyValue);
		
		if (valueAsString != null)
			try {
				propertyValue.setValue(Long.valueOf(valueAsString));
			} catch(Throwable e) {
				LOG.debug("Couldn't read decimal property " + propertyValue.getProperty().getLabel(), e);
				propertyValue.setValue(null);
			}
	}

	@Override
	public void doWithReal(final RealValue propertyValue) {
		final String valueAsString = attributeValueAsString(propertyValue);
		
		if (valueAsString != null)
			try {
				propertyValue.setValue(Double.valueOf(valueAsString));
			} catch(Throwable e) {
				LOG.debug("Couldn't read real property " + propertyValue.getProperty().getLabel(), e);
				propertyValue.setValue(null);
			}
	}

	@Override
	public void doWithDate(final DateValue propertyValue) {
		final Object value = vertex.getProperty(propertyValue.getProperty().getName());
		
		if (value != null && value.getClass() == Date.class)
			propertyValue.setValue((Date) value);
		else
			propertyValue.setValue(null);
	}

	@Override
	public void doWithString(StringValue propertyValue) {
		propertyValue.setValue(attributeValueAsString(propertyValue));
	}

	@Override
	public void doWithText(TextValue propertyValue) {
		propertyValue.setValue(attributeValueAsString(propertyValue));
	}
	
	private String attributeValueAsString(final IPropertyValue<?> propertyValue) {
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
		final Property property = propertyValue.getProperty();
		final String propertyName = property.getName();
		
		for (Vertex referencingVertex : vertex.getVertices(Direction.OUT, propertyName)) {
			final IEntity entity = new Entity(schema, referencingVertex);
			
			if (property.getType().isConform(entity.getType()))
				propertyValue.setValue(entity);
		}
	}

	@Override
	public void doWithAssociations(Associations propertyValue) {
		final HashSet<IEntity> associations = new HashSet<>();
		final Property property = propertyValue.getProperty();
		final String propertyName = property.getName();
		
		for (Vertex referencingVertex : vertex.getVertices(Direction.OUT, propertyName)) {
			final IEntity entity = new Entity(schema, referencingVertex);
			
			if (property.getType().isConform(entity.getType()))
				associations.add(entity);
		}
		
		propertyValue.setValue(associations);
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		for (IPropertyValue<?> value : getValues()) {
			if (!value.getProperty().getType().isUserDefined()) { // attrs only
				final String valueStr = value.toString();
				
				if (!valueStr.isEmpty()) {
					if (sb.length() > 0)
						sb.append(", ");
					
					sb.append(valueStr);
				}
			}
		}
		
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		final Entity other = (Entity) obj;
		
		return id.equals(other.id);
	}
}