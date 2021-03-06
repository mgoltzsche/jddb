package de.algorythm.jddb.model.dao.impl.blueprints.propertyVisitor;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

import de.algorythm.jddb.model.dao.IPropertyValueLoader;
import de.algorythm.jddb.model.entity.IEntity;
import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.entity.IPropertyValueVisitor;
import de.algorythm.jddb.model.meta.MEntityType;
import de.algorythm.jddb.model.meta.MProperty;

public class LoadVisitor<V extends IEntity<P,REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> implements IPropertyValueVisitor<REF>, IPropertyValueLoader<REF> {
	
	static private final Logger LOG = LoggerFactory.getLogger(LoadVisitor.class);
	
	private final Vertex vertex;
	private final String id;
	private final MEntityType type;
	private final IDAOVisitorContext<V,P,REF> dao;
	
	public LoadVisitor(final Vertex vertex, final String id, final MEntityType type, final IDAOVisitorContext<V,P,REF> dao) {
		this.vertex = vertex;
		this.id = id;
		this.type = type;
		this.dao = dao;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public MEntityType getType() {
		return type;
	}
	
	@Override
	public Collection<REF> loadReferringEntities() {
		return dao.loadReferringEntities(id, vertex);
	}
	
	@Override
	public void load(final IPropertyValue<?,REF> propertyValue) {
		propertyValue.visit(this);
	}
	
	@Override
	public void doWithAssociation(final IPropertyValue<REF,REF> propertyValue) {
		final MProperty property = propertyValue.getProperty();
		final String propertyName = property.getName();
		
		for (Vertex referencingVertex : vertex.getVertices(Direction.OUT, propertyName)) {
			final REF entityRef = dao.createEntityReference(referencingVertex);
			
			if (property.getType().isConform(entityRef.getType())) {
				propertyValue.setValue(entityRef);
				return;
			}
		}
		
		propertyValue.setValue(null);
	}

	@Override
	public void doWithAssociations(final IPropertyValue<Collection<REF>,REF> propertyValue) {
		final LinkedHashSet<REF> associations = new LinkedHashSet<>();
		final MProperty property = propertyValue.getProperty();
		final String propertyName = property.getName();
		
		for (Vertex referencingVertex : vertex.getVertices(Direction.OUT, propertyName)) {
			final REF entityRef = dao.createEntityReference(referencingVertex);
			
			if (property.getType().isConform(entityRef.getType()))
				associations.add(entityRef);
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
				propertyValue.setValue(Boolean.FALSE);
				LOG.debug("Couldn't read boolean property " + propertyValue.getProperty().getLabel(), e);
			}
	}
	
	@Override
	public void doWithDecimal(final IPropertyValue<Long,?> propertyValue) {
		final String valueAsString = attributeValueAsString(propertyValue);
		
		if (valueAsString != null) {
			try {
				propertyValue.setValue(Long.valueOf(valueAsString));
			} catch(Throwable e) {
				LOG.debug("Couldn't read decimal property " + propertyValue.getProperty().getLabel(), e);
				propertyValue.setValue(null);
			}
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
	
	@Override
	public void doWithFile(final IPropertyValue<String,?> propertyValue) {
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
