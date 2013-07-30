package de.algorythm.jdoe.model.entity.impl;

import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class BooleanValue extends AbstractPropertyValue<Boolean> {

	static private final long serialVersionUID = -6636393944404667739L;

	public BooleanValue(final Property property) {
		super(property);
		value = false;
	}

	@Override
	public void doWithValue(final IPropertyValueVisitor visitor) {
		visitor.doWithBoolean(this);
	}
	
	@Override
	public String toString() {
		return value ? "ja" : "nein";
	}
}
