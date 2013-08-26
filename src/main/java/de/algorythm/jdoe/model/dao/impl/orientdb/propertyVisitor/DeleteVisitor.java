package de.algorythm.jdoe.model.dao.impl.orientdb.propertyVisitor;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import de.algorythm.jdoe.model.dao.impl.orientdb.IDAOVisitorContext;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class DeleteVisitor<REF extends IEntityReference, P extends IPropertyValue<?, REF>> extends IndexKeywordCollectingVisitor<REF> {
	
	private final IDAOVisitorContext<REF,P> dao;
	
	public DeleteVisitor(final IDAOVisitorContext<REF,P> dao, final Pattern wordPattern, final Set<String> indexKeywords) {
		super(wordPattern, indexKeywords);
		this.dao = dao;
	}
	
	@Override
	public void doWithAssociations(final IPropertyValue<Collection<REF>,REF> propertyValue) {
		if (propertyValue.getProperty().isContainment())
			for (REF entityRef : propertyValue.getValue())
				dao.delete(entityRef);
	}
	
	@Override
	public void doWithAssociation(final IPropertyValue<REF,REF> propertyValue) {
		if (propertyValue.getProperty().isContainment()) {
			final REF entityRef = propertyValue.getValue();
			
			if (entityRef != null)
				dao.delete(entityRef);
		}
	}
}
