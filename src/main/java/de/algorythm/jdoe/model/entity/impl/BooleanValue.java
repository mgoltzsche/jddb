package de.algorythm.jdoe.model.entity.impl;

import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class BooleanValue extends AbstractPropertyValue {

	static private final long serialVersionUID = -6636393944404667739L;

	private boolean value;

	public BooleanValue(final Property property) {
		super(property);
	}
	
	public boolean getValue() {
		return value;
	}

	public void setValue(final boolean value) {
		this.value = value;
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
