package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.entity.IAttributeValueVisitor;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueFactory;
import de.algorythm.jdoe.model.meta.Property;

public class TText extends AbstractAttributeType<String> {

	static private final long serialVersionUID = 8799483774046996115L;

	public TText() {
		super("text");
	}
	
	@Override
	public <E extends IEntityReference, P extends IPropertyValue<?,E>> P createPropertyValue(final Property property, final IPropertyValueFactory<E,P> factory) {
		return factory.createAttributeValue(property, this);
	}
	
	@Override
	public void doWithPropertyValue(final IPropertyValue<String,?> value,
			final IAttributeValueVisitor visitor) {
		visitor.doWithText(value);
	}
	
	@Override
	public void valueToString(final String value, final StringBuilder sb) {
		if (value != null)
			sb.append(value);
	}
}
