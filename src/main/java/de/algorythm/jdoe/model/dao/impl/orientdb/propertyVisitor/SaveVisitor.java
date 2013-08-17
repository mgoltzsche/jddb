package de.algorythm.jdoe.model.dao.impl.orientdb.propertyVisitor;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Pattern;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import de.algorythm.jdoe.model.dao.impl.orientdb.Entity;
import de.algorythm.jdoe.model.dao.impl.orientdb.IDAOVisitorContext;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.impl.Association;
import de.algorythm.jdoe.model.entity.impl.Associations;
import de.algorythm.jdoe.model.entity.impl.BooleanValue;
import de.algorythm.jdoe.model.entity.impl.DateValue;
import de.algorythm.jdoe.model.entity.impl.DecimalValue;
import de.algorythm.jdoe.model.entity.impl.RealValue;
import de.algorythm.jdoe.model.entity.impl.StringValue;
import de.algorythm.jdoe.model.entity.impl.TextValue;
import de.algorythm.jdoe.model.meta.Property;

public class SaveVisitor extends IndexKeywordCollectingVisitor {
	
	private final Vertex vertex;
	private final Collection<Entity> savedEntities;
	private final IDAOVisitorContext dao;
	
	public SaveVisitor(final IDAOVisitorContext dao, final Vertex vertex, final Collection<Entity> savedEntities, final Pattern wordPattern, final Set<String> indexKeywords) {
		super(wordPattern, indexKeywords);
		this.dao = dao;
		this.vertex = vertex;
		this.savedEntities = savedEntities;
	}
	
	@Override
	public void doWithAssociations(Associations propertyValue) {
		if (!propertyValue.isChanged())
			return;
		
		final Property property = propertyValue.getProperty();
		final String propertyName = property.getName();
		final LinkedList<Edge> existingEdges = new LinkedList<Edge>();
		
		for (Edge edge : vertex.getEdges(Direction.OUT, propertyName))
			existingEdges.add(edge);
		
		for (IEntity refEntity : propertyValue.getValue()) {
			if (!refEntity.isPersisted()) // save new entity
				dao.saveInTransaction(refEntity, savedEntities);
			
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
		if (!propertyValue.isChanged())
			return;
		
		final Property property = propertyValue.getProperty();
		final String propertyName = property.getName();
		final Entity refEntity = (Entity) propertyValue.getValue();
		Vertex refVertex = null;
		
		if (refEntity != null) {
			if (!refEntity.isPersisted())
				dao.saveInTransaction(refEntity, savedEntities);
			
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
		super.doWithBoolean(propertyValue);
	}

	@Override
	public void doWithDecimal(DecimalValue propertyValue) {
		writeAttributeValue(propertyValue);
		super.doWithDecimal(propertyValue);
	}

	@Override
	public void doWithReal(RealValue propertyValue) {
		writeAttributeValue(propertyValue);
		super.doWithReal(propertyValue);
	}

	@Override
	public void doWithDate(DateValue propertyValue) {
		writeAttributeValue(propertyValue);
		super.doWithDate(propertyValue);
	}

	@Override
	public void doWithString(StringValue propertyValue) {
		writeAttributeValue(propertyValue);
		super.doWithString(propertyValue);
	}

	@Override
	public void doWithText(TextValue propertyValue) {
		writeAttributeValue(propertyValue);
		super.doWithText(propertyValue);
	}
	
	private void writeAttributeValue(IPropertyValue<?> propertyValue) {
		final Property property = propertyValue.getProperty();
		final String propertyName = property.getName();
		final Object newValue = propertyValue.getValue();
		
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
			final IEntity referredEntity = dao.createEntity(referredVertex);
			
			if (property.getType().isConform(referredEntity.getType()))
				dao.deleteInTransaction(referredEntity);
		}
	}
}