package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.impl.BooleanValue;
import de.algorythm.jdoe.model.meta.Property;

public class TBoolean extends AbstractAttributeType  {

	static private final long serialVersionUID = 248266786995111398L;

	public TBoolean() {
		super("boolean");
	}

	@Override
	public IPropertyValue createPropertyValue(final Property property) {
		return new BooleanValue(property);
	}
}
