package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.dao.IPropertyValueFactory;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class TString extends AbstractAttributeType<String> {

	static private final long serialVersionUID = 6086451869372645461L;

	public TString() {
		super("string");
	}
	
	@Override
	public <P extends IPropertyValue<?>> P createPropertyValue(final Property property, final IPropertyValueFactory<P> factory) {
		return factory.createPropertyValue(property, this);
	}

	@Override
	public void doWithPropertyValue(final IPropertyValue<String> value,
			final IPropertyValueVisitor visitor) {
		visitor.doWithString(value);
	}
	
	@Override
	public void valueToString(final String value, final StringBuilder sb) {
		if (value != null)
			sb.append(value);
	}
}
