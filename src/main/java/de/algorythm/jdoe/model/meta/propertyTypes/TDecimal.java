package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.entity.IAttributeValueVisitor;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueFactory;
import de.algorythm.jdoe.model.meta.Property;

public class TDecimal extends AbstractAttributeType<Long> {

	static private final long serialVersionUID = -4545233017943271599L;

	public TDecimal() {
		super("decimal");
	}
	
	@Override
	public <E extends IEntityReference, P extends IPropertyValue<?,E>> P createPropertyValue(final Property property, final IPropertyValueFactory<E,P> factory) {
		return factory.createAttributeValue(property, this);
	}

	@Override
	public void doWithPropertyValue(final IPropertyValue<Long,?> value,
			final IAttributeValueVisitor visitor) {
		visitor.doWithDecimal(value);
	}
	
	@Override
	public void valueToString(final Long value, final StringBuilder sb) {
		if (value != null)
			sb.append(value);
	}
}
