package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.dao.IPropertyValueFactory;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class TDecimal extends AbstractAttributeType<Long> {

	static private final long serialVersionUID = -4545233017943271599L;

	public TDecimal() {
		super("decimal");
	}
	
	@Override
	public <P extends IPropertyValue<?>> P createPropertyValue(final Property property, final IPropertyValueFactory<P> factory) {
		return factory.createPropertyValue(property, this);
	}

	@Override
	public void doWithPropertyValue(final IPropertyValue<Long> value,
			final IPropertyValueVisitor visitor) {
		visitor.doWithDecimal(value);
	}
	
	@Override
	public void valueToString(final Long value, final StringBuilder sb) {
		if (value != null)
			sb.append(value);
	}
}
