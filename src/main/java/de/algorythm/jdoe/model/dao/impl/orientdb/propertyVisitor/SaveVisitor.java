package de.algorythm.jdoe.model.dao.impl.orientdb.propertyVisitor;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import de.algorythm.jdoe.model.dao.impl.orientdb.IDAOVisitorContext;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.Property;

public class SaveVisitor<V extends IEntity<P,REF>, P extends IPropertyValue<?, REF>, REF extends IEntityReference> extends IndexKeywordCollectingVisitor<REF> {
	
	private final Vertex vertex;
	private final Map<V, Vertex> savedEntities;
	private final IDAOVisitorContext<V,P,REF> dao;
	
	public SaveVisitor(final IDAOVisitorContext<V,P,REF> dao, final Vertex vertex, final Map<V, Vertex> savedEntities, final Pattern wordPattern, final Set<String> indexKeywords) {
		super(wordPattern, indexKeywords);
		this.dao = dao;
		this.vertex = vertex;
		this.savedEntities = savedEntities;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void doWithAssociations(final IPropertyValue<Collection<REF>,REF> propertyValue) {
		final Property property = propertyValue.getProperty();
		final String propertyName = property.getName();
		final LinkedList<Edge> existingEdges = new LinkedList<Edge>();
		
		for (Edge edge : vertex.getEdges(Direction.OUT, propertyName))
			existingEdges.add(edge);
		
		for (REF entityRef : propertyValue.getValue()) {
			Vertex refVertex;
			
			try {
				// associate existing entity
				refVertex = dao.findVertex(entityRef);
				
				// check existing edge
				boolean edgeAlreadyExists = false;
				
				final Iterator<Edge> existingEdgeIter = existingEdges.iterator();
				
				while (existingEdgeIter.hasNext()) {
					final Edge edge = existingEdgeIter.next();
					
					if (edge.getVertex(Direction.IN).equals(refVertex)) {
						edgeAlreadyExists = true;
						existingEdgeIter.remove();
						break;
					}
				}
				
				if (!edgeAlreadyExists) // add new edge
					vertex.addEdge(propertyName, refVertex);
			} catch(IllegalArgumentException e) {
				// save and associate new entity
				final V newEntity;
				
				try {
					newEntity = (V) entityRef;
				} catch(ClassCastException cce) {
					throw new IllegalArgumentException("referred entity " + entityRef + "(" + entityRef.getId() + ") does not exist and cannot be created since the referred entity does not implement the entity type interface V extending " + IEntity.class);
				}
				
				refVertex = dao.save(newEntity, savedEntities);
				
				vertex.addEdge(propertyName, refVertex);
			}
		}
		
		// remove invalid edges
		for (Edge edge : existingEdges)
			deleteEdge(property, edge);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void doWithAssociation(final IPropertyValue<REF,REF> propertyValue) {
		final Property property = propertyValue.getProperty();
		final String propertyName = property.getName();
		final REF entityRef = propertyValue.getValue();
		Vertex refVertex = null;
		
		if (entityRef != null) {
			try {
				// associate existing entity
				refVertex = dao.findVertex(entityRef);
			} catch(IllegalArgumentException e) {
				// save and associate new entity
				final V newEntity;
				
				try {
					newEntity = (V) entityRef;
				} catch(ClassCastException cce) {
					throw new IllegalArgumentException("referred entity " + entityRef + "(" + entityRef.getId() + ") does not exist and cannot be created since the referred entity does not implement the entity type interface V extending " + IEntity.class);
				}
				
				refVertex = dao.save(newEntity, savedEntities);
			}
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
	public void doWithBoolean(final IPropertyValue<Boolean,?> propertyValue) {
		writeAttributeValue(propertyValue);
		super.doWithBoolean(propertyValue);
	}

	@Override
	public void doWithDecimal(final IPropertyValue<Long,?> propertyValue) {
		writeAttributeValue(propertyValue);
		super.doWithDecimal(propertyValue);
	}

	@Override
	public void doWithReal(final IPropertyValue<Double,?> propertyValue) {
		writeAttributeValue(propertyValue);
		super.doWithReal(propertyValue);
	}

	@Override
	public void doWithDate(final IPropertyValue<Date,?> propertyValue) {
		writeAttributeValue(propertyValue);
		super.doWithDate(propertyValue);
	}

	@Override
	public void doWithString(final IPropertyValue<String,?> propertyValue) {
		writeAttributeValue(propertyValue);
		super.doWithString(propertyValue);
	}

	@Override
	public void doWithText(final IPropertyValue<String,?> propertyValue) {
		writeAttributeValue(propertyValue);
		super.doWithText(propertyValue);
	}
	
	private void writeAttributeValue(IPropertyValue<?,?> propertyValue) {
		final Property property = propertyValue.getProperty();
		final String propertyName = property.getName();
		final Object newValue = propertyValue.getValue();
		final Object oldValue = vertex.getProperty(propertyName);
		
		if (oldValue == null && newValue == null || oldValue != null && oldValue.equals(newValue))
			return;
		
		// persist new value
		if (newValue == null)
			vertex.removeProperty(propertyName);
		else
			vertex.setProperty(propertyName, newValue);
	}
	
	private void deleteEdge(final Property property, final Edge edge) {
		System.out.println("delete edge " + property.getLabel() + ", containment: " + property.isContainment());
		final Vertex referredVertex = edge.getVertex(Direction.IN);
		
		edge.remove();
		
		if (property.isContainment()) {
			final REF referredEntity = dao.createEntityReference(referredVertex);
			
			if (property.getType().isConform(referredEntity.getType()))
				dao.delete(referredEntity);
		}
	}
}