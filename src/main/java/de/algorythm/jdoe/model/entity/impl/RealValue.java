package de.algorythm.jdoe.model.entity.impl;

import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class RealValue extends AbstractPropertyValue<Double> {

	static private final long serialVersionUID = 5194733075415187873L;
	
	public RealValue(final Property property) {
		super(property);
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
