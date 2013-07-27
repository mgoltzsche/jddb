package de.algorythm.jdoe.model.entity.impl;

import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class DecimalValue extends AbstractPropertyValue {

	static private final long serialVersionUID = -8344833530871931352L;
	
	private Long value;

	public DecimalValue(final Property property) {
		super(property);
	}
	
	public Long getValue() {
		return value;
	}

	public void setValue(final Long value) {
		this.value = value;
	}

	@Override
	public void doWithValue(final IPropertyValueVisitor visitor) {
		visitor.doWithDecimal(this);
	}
	
	@Override
	public String toString() {
		return value == null ? null : value.toString();
	}
}
