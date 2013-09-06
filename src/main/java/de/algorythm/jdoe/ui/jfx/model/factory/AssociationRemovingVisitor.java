package de.algorythm.jdoe.ui.jfx.model.factory;

import java.util.Collection;
import java.util.Date;

import de.algorythm.jdoe.cache.IObjectCache;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;

public class AssociationRemovingVisitor implements IPropertyValueVisitor<FXEntityReference> {

	static public void removeAssociationsTo(final FXEntity removedEntity, final IObjectCache<FXEntity> entityCache) {
		for (FXEntityReference referringEntity : removedEntity.getReferringEntities()) {
			final FXEntity cachedEntity = entityCache.get(referringEntity.getId());
			
			if (cachedEntity != null) {
				final AssociationRemovingVisitor visitor = new AssociationRemovingVisitor(cachedEntity);
				
				for (IFXPropertyValue<?> propertyValue : cachedEntity.getValues())
					propertyValue.visit(visitor);
			}
		}
	}
	
	private final FXEntity entity;
	
	private AssociationRemovingVisitor(final FXEntity entity) {
		this.entity = entity;
	}
	
	@Override
	public void doWithAssociation(IPropertyValue<FXEntityReference, FXEntityReference> propertyValue) {
		final FXEntityReference entityRef = propertyValue.getValue();
		
		if (entityRef != null && entityRef.equals(entity))
			propertyValue.setValue(null);
	}

	@Override
	public void doWithAssociations(final IPropertyValue<Collection<FXEntityReference>, FXEntityReference> propertyValue) {
		propertyValue.getValue().remove(entity);
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
