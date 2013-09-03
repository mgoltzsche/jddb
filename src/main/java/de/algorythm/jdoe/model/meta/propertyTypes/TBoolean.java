package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.bundle.Bundle;
import de.algorythm.jdoe.model.entity.IAttributeValueVisitor;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class TBoolean extends AbstractAttributeType<Boolean> {

	static private final long serialVersionUID = 248266786995111398L;

	public TBoolean() {
		super("boolean");
	}
	
	@Override
	public void visit(final IPropertyValue<Boolean,?> value,
			final IAttributeValueVisitor visitor) {
		visitor.doWithBoolean(value);
	}
	
	@Override
	public void valueToString(final Boolean value, final StringBuilder sb) {
		sb.append(value ? Bundle.getInstance().yes : Bundle.getInstance().no);
	}
}
