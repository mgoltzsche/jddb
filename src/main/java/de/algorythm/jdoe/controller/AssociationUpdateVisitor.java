package de.algorythm.jdoe.controller;

import java.util.Date;
import java.util.Iterator;

import de.algorythm.jdoe.model.dao.ModelChange;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociation;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociations;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValueVisitor;

public class AssociationUpdateVisitor implements IFXPropertyValueVisitor {

	private final ModelChange<FXEntity, IFXPropertyValue<?>, FXEntityReference> change;
	
	public AssociationUpdateVisitor(final ModelChange<FXEntity, IFXPropertyValue<?>, FXEntityReference> change) {
		this.change = change;
	}
	
	private boolean update(final FXEntityReference entityRef) {
		if (change.getDeleted().contains(entityRef))
			return false;
		
		final FXEntity saved = change.getSaved().get(entityRef.getId());
		
		if (saved != null)
			entityRef.assign(saved);
		
		return true;
	}
	
	@Override
	public void doWithAssociation(final FXAssociation propertyValue) {
		final FXEntityReference entityRef = propertyValue.getValue();
		
		if (entityRef != null && !update(entityRef))
			propertyValue.setValue(null);
	}

	@Override
	public void doWithAssociations(final FXAssociations propertyValue) {
		final Iterator<FXEntityReference> iter = propertyValue.getValue().iterator();
		
		while(iter.hasNext())
			if (!update(iter.next()))
				iter.remove();
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
