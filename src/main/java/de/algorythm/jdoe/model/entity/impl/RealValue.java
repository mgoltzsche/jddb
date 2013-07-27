package de.algorythm.jdoe.model.entity.impl;

import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class RealValue extends AbstractPropertyValue {

	static private final long serialVersionUID = 5194733075415187873L;
	
	private Double value;

	public RealValue(final Property property) {
		super(property);
	}
	
	public Double getValue() {
		return value;
	}

	public void setValue(final Double value) {
		this.value = value;
	}

	@Override
	public void doWithValue(final IPropertyValueVisitor visitor) {
		visitor.doWithReal(this);
	}
	
	@Override
	public String toString() {
		return value == null ? null : String.format("%.2f", value);
	}
}
