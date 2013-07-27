package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.impl.DateValue;
import de.algorythm.jdoe.model.meta.Property;

public class TDate extends AbstractAttributeType  {

	static private final long serialVersionUID = -7251553097102848742L;

	public TDate() {
		super("date");
	}
	
	@Override
	public IPropertyValue createPropertyValue(final Property property) {
		return new DateValue(property);
	}
}
