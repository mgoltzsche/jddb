package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.impl.StringValue;
import de.algorythm.jdoe.model.meta.Property;

public class TString extends AbstractAttributeType  {

	static private final long serialVersionUID = 6086451869372645461L;

	public TString() {
		super("string");
	}
	
	@Override
	public IPropertyValue createPropertyValue(final Property property) {
		return new StringValue(property);
	}
}
