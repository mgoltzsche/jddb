package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.entity.IAttributeValueVisitor;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class TDecimal extends AbstractAttributeType<Long> {

	static private final long serialVersionUID = -4545233017943271599L;

	public TDecimal() {
		super("decimal");
	}

	@Override
	public void visit(final IPropertyValue<Long,?> value,
			final IAttributeValueVisitor visitor) {
		visitor.doWithDecimal(value);
	}
	
	@Override
	public void valueToString(final Long value, final StringBuilder sb) {
		if (value != null)
			sb.append(value);
	}
}
