package de.algorythm.jdoe.model.entity.impl;

import java.util.Date;

import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class DateValue extends AbstractPropertyValue<Date> {

	static private final long serialVersionUID = -3672728711917060538L;
	
	public DateValue(final Property property) {
		super(property);
	}

	@Override
	public void doWithValue(final IPropertyValueVisitor visitor) {
		visitor.doWithDate(this);
	}
	
	@Override
	public String toString() {
		return value == null ? null : value.toString();
	}

}