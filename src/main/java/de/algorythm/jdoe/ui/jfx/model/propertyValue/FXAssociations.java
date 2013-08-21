package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import java.util.ArrayList;
import java.util.Collection;

import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public class FXAssociations extends AbstractFXPropertyValue<Collection<FXEntityReference>> {

	static private final long serialVersionUID = -2428408831904938958L;

	public FXAssociations(Property property) {
		super(property);
	}
	
	@Override
	public void doWithValue(final IPropertyValueVisitor<FXEntityReference> visitor) {
		visitor.doWithAssociations(this);
	}
	
	@Override
	public void toString(final StringBuilder sb) {
		sb.append(String.valueOf(getValue().size()));
	}

	@Override
	protected boolean valueChanged(final Collection<FXEntityReference> oldValue,
			final Collection<FXEntityReference> newValue) {
		return !oldValue.containsAll(newValue) || !newValue.containsAll(oldValue);
	}

	@Override
	public IFXPropertyValue<Collection<FXEntityReference>> copy() {
		final FXAssociations copy = new FXAssociations(getProperty());
		
		copy.setValue(new ArrayList<>(copy.getValue()));
		
		return copy;
	}
}
