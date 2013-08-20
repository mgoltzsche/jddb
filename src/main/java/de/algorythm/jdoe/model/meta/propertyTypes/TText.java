package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.dao.IPropertyValueFactory;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class TText extends AbstractAttributeType<String> {

	static private final long serialVersionUID = 8799483774046996115L;

	public TText() {
		super("text");
	}
	
	@Override
	public <P extends IPropertyValue<?>> P createPropertyValue(final Property property, final IPropertyValueFactory<P> factory) {
		return factory.createPropertyValue(property, this);
	}
	
	@Override
	public void doWithPropertyValue(final IPropertyValue<String> value,
			final IPropertyValueVisitor visitor) {
		visitor.doWithText(value);
	}
	
	@Override
	public void valueToString(final String value, final StringBuilder sb) {
		if (value != null)
			sb.append(value);
	}
}
