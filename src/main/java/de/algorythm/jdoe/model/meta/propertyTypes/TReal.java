package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.entity.IAttributeValueVisitor;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class TReal extends AbstractAttributeType<Double> {

	static private final long serialVersionUID = 4068458404531019807L;

	public TReal() {
		super("real");
	}

	@Override
	public void doWithPropertyValue(final IPropertyValue<Double,?> value,
			final IAttributeValueVisitor visitor) {
		visitor.doWithReal(value);
	}
	
	@Override
	public void valueToString(final Double value, final StringBuilder sb) {
		if (value != null)
			sb.append(String.format("%.2f", value));
	}
}