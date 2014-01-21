package de.algorythm.jddb.ui.jfx.model.factory;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.entity.IPropertyValueVisitor;
import de.algorythm.jddb.ui.jfx.model.FXEntity;
import de.algorythm.jddb.ui.jfx.model.FXEntityReference;
import de.algorythm.jddb.ui.jfx.model.propertyValue.IFXPropertyValue;

public class ReferredEntityCollectingVisitor implements IPropertyValueVisitor<FXEntityReference> {

	static public Set<FXEntityReference> referredEntities(final FXEntity entity) {
		final Set<FXEntityReference> referredEntities = new HashSet<>();
		final ReferredEntityCollectingVisitor visitor = new ReferredEntityCollectingVisitor(referredEntities);
		
		for (IFXPropertyValue<?> propertyValue : entity.getValues())
			propertyValue.visit(visitor);
		
		return referredEntities;
	}
	
	private final Set<FXEntityReference> referredEntities;
	
	private ReferredEntityCollectingVisitor(final Set<FXEntityReference> referredEntities) {
		this.referredEntities = referredEntities;
	}
	
	@Override
	public void doWithAssociation(IPropertyValue<FXEntityReference, FXEntityReference> propertyValue) {
		final FXEntityReference entityRef = propertyValue.getValue();
		
		if (entityRef != null)
			referredEntities.add(entityRef);
	}

	@Override
	public void doWithAssociations(final IPropertyValue<Collection<FXEntityReference>, FXEntityReference> propertyValue) {
		referredEntities.addAll(propertyValue.getValue());
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
	
	@Override
	public void doWithFile(IPropertyValue<String, ?> propertyValue) {}
}
