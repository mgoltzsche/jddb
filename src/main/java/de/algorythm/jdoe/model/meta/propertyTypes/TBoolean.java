package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.bundle.Bundle;
import de.algorythm.jdoe.model.entity.IAttributeValueVisitor;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueFactory;
import de.algorythm.jdoe.model.meta.Property;

public class TBoolean extends AbstractAttributeType<Boolean> {

	static private final long serialVersionUID = 248266786995111398L;

	public TBoolean() {
		super("boolean");
	}

	@Override
	public <E extends IEntityReference, P extends IPropertyValue<?,E>> P createPropertyValue(final Property property, final IPropertyValueFactory<E,P> factory) {
		return factory.createAttributeValue(property, this);
	}

	@Override
	public void doWithPropertyValue(final IPropertyValue<Boolean,?> value,
			final IAttributeValueVisitor visitor) {
		visitor.doWithBoolean(value);
	}
	
	@Override
	public void valueToString(final Boolean value, final StringBuilder sb) {
		sb.append(value ? Bundle.getInstance().yes : Bundle.getInstance().no);
	}
}
