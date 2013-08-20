package de.algorythm.jdoe.model.dao.impl.orientdb.propertyVisitor;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import de.algorythm.jdoe.model.dao.impl.orientdb.IDAOVisitorContext;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class DeleteVisitor<E extends IEntityReference> extends IndexKeywordCollectingVisitor<E> {
	
	private final IDAOVisitorContext<E> dao;
	
	public DeleteVisitor(final IDAOVisitorContext<E> dao, final Pattern wordPattern, final Set<String> indexKeywords) {
		super(wordPattern, indexKeywords);
		this.dao = dao;
	}
	
	@Override
	public void doWithAssociations(final IPropertyValue<Collection<E>,E> propertyValue) {
		if (propertyValue.getProperty().isContainment())
			for (E entityRef : propertyValue.getValue())
				dao.deleteInTransaction(entityRef);
	}
	
	@Override
	public void doWithAssociation(final IPropertyValue<E,E> propertyValue) {
		if (propertyValue.getProperty().isContainment()) {
			final E entityRef = propertyValue.getValue();
			
			if (entityRef != null)
				dao.deleteInTransaction(entityRef);
		}
	}
}
