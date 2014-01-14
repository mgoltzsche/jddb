package de.algorythm.jdoe.ui.jfx.model.factory;

import de.algorythm.jdoe.cache.IObjectCache;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.BooleanFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.DateFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.DecimalFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociation;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociations;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FileFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValueVisitor;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.RealFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.StringFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.TextFXAttributeValue;

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
	public void doWithBoolean(BooleanFXAttributeValue propertyValue) {}

	@Override
	public void doWithDecimal(DecimalFXAttributeValue propertyValue) {}

	@Override
	public void doWithReal(RealFXAttributeValue propertyValue) {}

	@Override
	public void doWithDate(DateFXAttributeValue propertyValue) {}

	@Override
	public void doWithString(StringFXAttributeValue propertyValue) {}

	@Override
	public void doWithText(TextFXAttributeValue propertyValue) {}
	
	@Override
	public void doWithFile(FileFXAttributeValue propertyValue) {}
}
