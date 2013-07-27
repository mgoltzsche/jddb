package de.algorythm.jdoe.model.entity.impl;

import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class TextValue extends StringValue {

	static private final long serialVersionUID = 670020384752110425L;

	public TextValue(final Property property) {
		super(property);
	}

	@Override
	public void doWithValue(final IPropertyValueVisitor visitor) {
		visitor.doWithText(this);
	}
}
