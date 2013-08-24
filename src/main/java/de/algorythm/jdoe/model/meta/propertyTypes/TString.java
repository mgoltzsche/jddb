package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.entity.IAttributeValueVisitor;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueFactory;
import de.algorythm.jdoe.model.meta.Property;

public class TString extends AbstractAttributeType<String> {

	static private final long serialVersionUID = 6086451869372645461L;

	public TString() {
		super("string");
	}
	
	@Override
	public <E extends IEntityReference, P extends IPropertyValue<?,E>> P createPropertyValue(final Property property, final IPropertyValueFactory<E,P> factory) {
		return factory.createAttributeValue(property, this);
	}

	@Override
	public void doWithPropertyValue(final IPropertyValue<String,?> value,
			final IAttributeValueVisitor visitor) {
		visitor.doWithString(value);
	}
	
	@Override
	public void valueToString(final String value, final StringBuilder sb) {
		if (value != null)
			sb.append(value);
	}
}
