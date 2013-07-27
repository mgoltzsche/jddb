package de.algorythm.jdoe.model.entity.impl;

import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class StringValue extends AbstractPropertyValue {

	static private final long serialVersionUID = 262826380374258830L;
	
	private String value;

	public StringValue(final Property property) {
		super(property);
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public void doWithValue(final IPropertyValueVisitor visitor) {
		visitor.doWithString(this);
	}
	
	@Override
	public String toString() {
		return value;
	}
}
