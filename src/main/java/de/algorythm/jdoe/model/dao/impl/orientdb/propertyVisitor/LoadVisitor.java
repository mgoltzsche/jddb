package de.algorythm.jdoe.model.dao.impl.orientdb.propertyVisitor;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

import de.algorythm.jdoe.model.dao.impl.orientdb.IDAOVisitorContext;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class LoadVisitor<REF extends IEntityReference, P extends IPropertyValue<?, REF>> implements IPropertyValueVisitor<REF> {
	
	static private final Logger LOG = LoggerFactory.getLogger(LoadVisitor.class);
	
	private final Vertex vertex;
	private final IDAOVisitorContext<REF,P> dao;
	
	public LoadVisitor(final Vertex vertex, final IDAOVisitorContext<REF,P> dao) {
		this.vertex = vertex;
		this.dao = dao;
	}
	
	@Override
	public void doWithAssociation(final IPropertyValue<REF,REF> propertyValue) {
		final Property property = propertyValue.getProperty();
		final String propertyName = property.getName();
		
		for (Vertex referencingVertex : vertex.getVertices(Direction.OUT, propertyName)) {
			final REF entityRef = dao.createEntityReference(referencingVertex);
			
			if (property.getType().isConform(entityRef.getType()))
				propertyValue.setValue(entityRef);
		}
	}

	@Override
	public void doWithAssociations(final IPropertyValue<Collection<REF>,REF> propertyValue) {
		final LinkedHashSet<REF> associations = new LinkedHashSet<>();
		final Property property = propertyValue.getProperty();
		final String propertyName = property.getName();
		
		for (Vertex referencingVertex : vertex.getVertices(Direction.OUT, propertyName)) {
			final REF entity = dao.createEntityReference(referencingVertex);
			
			if (property.getType().isConform(entity.getType()))
				associations.add(entity);
		}
		
		propertyValue.setValue(associations);
	}
	
	@Override
	public void doWithBoolean(final IPropertyValue<Boolean,?> propertyValue) {
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
	public void doWithDecimal(final IPropertyValue<Long,?> propertyValue) {
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
	public void doWithReal(final IPropertyValue<Double,?> propertyValue) {
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
	public void doWithDate(final IPropertyValue<Date,?> propertyValue) {
		final Object value = vertex.getProperty(propertyValue.getProperty().getName());
		
		if (value != null && value.getClass() == Date.class)
			propertyValue.setValue((Date) value);
		else
			propertyValue.setValue(null);
	}

	@Override
	public void doWithString(final IPropertyValue<String,?> propertyValue) {
		propertyValue.setValue(attributeValueAsString(propertyValue));
	}

	@Override
	public void doWithText(final IPropertyValue<String,?> propertyValue) {
		propertyValue.setValue(attributeValueAsString(propertyValue));
	}
	
	private String attributeValueAsString(final IPropertyValue<?,?> propertyValue) {
		final Object value = vertex.getProperty(propertyValue.getProperty().getName());
		
		if (value == null)
			return null;
		else {
			final String valueStr = value.toString();
			
			return valueStr.isEmpty() ? null : valueStr;
		}
	}
}
