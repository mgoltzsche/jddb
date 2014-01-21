package de.algorythm.jddb.model.dao.impl.blueprints.propertyVisitor;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import de.algorythm.jddb.model.entity.IEntity;
import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;

public class DeleteVisitor<V extends IEntity<P,REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> extends IndexKeywordCollectingVisitor<REF> {
	
	private final IDAOVisitorContext<V,P,REF> dao;
	
	public DeleteVisitor(final IDAOVisitorContext<V,P,REF> dao, final Pattern wordPattern, final Set<String> indexKeywords) {
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
