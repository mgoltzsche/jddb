package de.algorythm.jdoe.ui.jfx.model.factory;

import java.util.Date;

import de.algorythm.jdoe.cache.IObjectCache;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociation;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociations;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValueVisitor;

public class AssociationRemovingVisitor implements IFXPropertyValueVisitor {

	static public void removeAssociationsTo(final FXEntity removedEntity, final IObjectCache<FXEntity> entityCache) {
		for (FXEntityReference referringEntity : removedEntity.getReferringEntities()) {
			final FXEntity cachedEntity = entityCache.get(referringEntity.getId());
			
			if (cachedEntity != null)
				removeAssociationsTo(removedEntity, cachedEntity);
		}
	}
	
	static public void removeAssociationsTo(final FXEntity removedEntity, final FXEntity entity) {
		final AssociationRemovingVisitor visitor = new AssociationRemovingVisitor(removedEntity);
		
		for (IFXPropertyValue<?> propertyValue : entity.getValues())
			propertyValue.visit(visitor);
	}
	
	private final FXEntity entity;
	
	private AssociationRemovingVisitor(final FXEntity entity) {
		this.entity = entity;
	}
	
	@Override
	public void doWithAssociation(FXAssociation propertyValue) {
		final FXEntityReference entityRef = propertyValue.getValue();
		
		if (entityRef != null && entityRef.equals(entity))
			propertyValue.setValue(null);
	}

	@Override
	public void doWithAssociations(FXAssociations propertyValue) {
		propertyValue.valueProperty().remove(entity);
	}
	
	@Override
	public void doWithBoolean(IPropertyValue<Boolean, ?> propertyValue) {}

	@Override
	public void doWithDecimal(IPropertyValue<Long, ?> propertyValue) {}

	@Override
	public void doWithReal(IPropertyValue<Double, ?> propertyValue) {}

	@Override
	public void doWithDate(IPropertyValue<Date, ?> propertyValue) {}

	@Override
	public void doWithString(IPropertyValue<String, ?> propertyValue) {}

	@Override
	public void doWithText(IPropertyValue<String, ?> propertyValue) {}
}
