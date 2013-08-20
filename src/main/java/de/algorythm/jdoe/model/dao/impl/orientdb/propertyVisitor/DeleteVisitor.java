package de.algorythm.jdoe.model.dao.impl.orientdb.propertyVisitor;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import de.algorythm.jdoe.model.dao.impl.orientdb.IDAOVisitorContext;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class DeleteVisitor extends IndexKeywordCollectingVisitor {
	
	private final IDAOVisitorContext dao;
	
	public DeleteVisitor(final IDAOVisitorContext dao, final Pattern wordPattern, final Set<String> indexKeywords) {
		super(wordPattern, indexKeywords);
		this.dao = dao;
	}
	
	@Override
	public void doWithAssociations(final IPropertyValue<Collection<IEntityReference>> propertyValue) {
		if (propertyValue.getProperty().isContainment())
			for (IEntityReference entityRef : propertyValue.getValue())
				dao.deleteInTransaction(entityRef);
	}
	
	@Override
	public void doWithAssociation(final IPropertyValue<IEntityReference> propertyValue) {
		if (propertyValue.getProperty().isContainment()) {
			final IEntityReference entityRef = propertyValue.getValue();
			
			if (entityRef != null)
				dao.deleteInTransaction(entityRef);
		}
	}
}
