package de.algorythm.jdoe.model.dao.impl.orientdb.propertyVisitor;

import java.util.Set;
import java.util.regex.Pattern;

import de.algorythm.jdoe.model.dao.impl.orientdb.IDAOVisitorContext;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.impl.Association;
import de.algorythm.jdoe.model.entity.impl.Associations;

public class DeleteVisitor extends IndexKeywordCollectingVisitor {
	
	private final IDAOVisitorContext dao;
	
	public DeleteVisitor(final IDAOVisitorContext dao, final Pattern wordPattern, final Set<String> indexKeywords) {
		super(wordPattern, indexKeywords);
		this.dao = dao;
	}
	
	@Override
	public void doWithAssociations(Associations propertyValue) {
		if (propertyValue.getProperty().isContainment())
			for (IEntity entity : propertyValue.getValue())
				dao.deleteInTransaction(entity);
	}
	
	@Override
	public void doWithAssociation(Association propertyValue) {
		if (propertyValue.getProperty().isContainment()) {
			final IEntity entity = propertyValue.getValue();
			
			if (entity != null)
				dao.deleteInTransaction(entity);
		}
	}
}
