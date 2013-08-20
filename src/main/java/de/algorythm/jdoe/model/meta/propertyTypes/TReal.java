package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.dao.IPropertyValueFactory;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class TReal extends AbstractAttributeType<Double> {

	static private final long serialVersionUID = 4068458404531019807L;

	public TReal() {
		super("real");
	}
	
	@Override
	public <P extends IPropertyValue<?>> P createPropertyValue(final Property property, final IPropertyValueFactory<P> factory) {
		return factory.createPropertyValue(property, this);
	}

	@Override
	public void doWithPropertyValue(final IPropertyValue<Double> value,
			final IPropertyValueVisitor visitor) {
		visitor.doWithReal(value);
	}
	
	@Override
	public void valueToString(final Double value, final StringBuilder sb) {
		if (value != null)
			sb.append(String.format("%.2f", value));
	}
}
