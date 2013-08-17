package de.algorythm.jdoe.model.entity.impl;

import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class DecimalValue extends AbstractPropertyValue<Long> {

	static private final long serialVersionUID = -8344833530871931352L;

	public DecimalValue(final Property property) {
		super(property);
	}

	@Override
	public void doWithValue(final IPropertyValueVisitor visitor) {
		visitor.doWithDecimal(this);
	}
	
	@Override
	public String toString() {
		return value == null ? EMPTY : value.toString();
	}
}
