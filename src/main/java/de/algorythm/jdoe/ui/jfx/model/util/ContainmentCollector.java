package de.algorythm.jdoe.ui.jfx.model.util;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;

public class ContainmentCollector implements IPropertyValueVisitor<FXEntityReference> {

	static public Set<FXEntity> allContainments(final FXEntity entity) {
		final Set<FXEntity> containments = new HashSet<>();
		
		new ContainmentCollector(containments).collectContainments(entity);
		
		return containments;
	}
	
	
	private final Set<FXEntity> containments;
	
	private ContainmentCollector(final Set<FXEntity> containments) {
		this.containments = containments;
	}
	
	private void collectContainments(final FXEntity entity) {
		for (IFXPropertyValue<?> value : entity.getValues())
			value.doWithValue(this);
	}

	@Override
	public void doWithAssociation(
			final IPropertyValue<FXEntityReference, FXEntityReference> propertyValue) {
		if (propertyValue.getProperty().isContainment())
			addContainment(propertyValue.getValue());
	}

	@Override
	public void doWithAssociations(
			final IPropertyValue<Collection<FXEntityReference>, FXEntityReference> propertyValue) {
		if (propertyValue.getProperty().isContainment())
			for (FXEntityReference entityRef : propertyValue.getValue())
				addContainment(entityRef);
	}
	
	private void addContainment(final FXEntityReference entityRef) {
		if (entityRef == null)
			return;
		
		final FXEntity containment = (FXEntity) entityRef;
		
		if (containments.add(containment))
			collectContainments(containment);
	}
	
	@Override
	public void doWithBoolean(final IPropertyValue<Boolean, ?> propertyValue) {}

	@Override
	public void doWithDecimal(final IPropertyValue<Long, ?> propertyValue) {}

	@Override
	public void doWithReal(final IPropertyValue<Double, ?> propertyValue) {}

	@Override
	public void doWithDate(final IPropertyValue<Date, ?> propertyValue) {}

	@Override
	public void doWithString(final IPropertyValue<String, ?> propertyValue) {}

	@Override
	public void doWithText(final IPropertyValue<String, ?> propertyValue) {}
}
