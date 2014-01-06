package de.algorythm.jdoe.ui.jfx.model.factory;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;

public class AssociationCollectingVisitor implements IPropertyValueVisitor<FXEntityReference> {

	static public final Set<FXEntityReference> associations(final FXEntity entity) {
		final Set<FXEntityReference> result = new HashSet<FXEntityReference>();
		final AssociationCollectingVisitor visitor = new AssociationCollectingVisitor(result);
		
		for (IFXPropertyValue<?> value : entity.getValues())
			value.visit(visitor);
		
		return result;
	}
	
	
	private final Set<FXEntityReference> entityRefs;
	
	private AssociationCollectingVisitor(final Set<FXEntityReference> entityRefs) {
		this.entityRefs = entityRefs;
	}
	
	@Override
	public void doWithAssociation(IPropertyValue<FXEntityReference, FXEntityReference> propertyValue) {
		final FXEntityReference entityRef = propertyValue.getValue();
		
		if (entityRef != null)
			entityRefs.add(entityRef);
	}

	@Override
	public void doWithAssociations(final IPropertyValue<Collection<FXEntityReference>, FXEntityReference> propertyValue) {
		entityRefs.addAll(propertyValue.getValue());
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
