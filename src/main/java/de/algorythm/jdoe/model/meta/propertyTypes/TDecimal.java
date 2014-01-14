package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueFactory;
import de.algorythm.jdoe.model.meta.MProperty;
import de.algorythm.jdoe.model.meta.TextAlignment;

public class TDecimal extends AbstractAttributeType<Long> {

	static private final long serialVersionUID = -4545233017943271599L;

	public TDecimal() {
		super("decimal");
	}

	@Override
	public <P extends IPropertyValue<?,? extends IEntityReference>> P createPropertyValue(final MProperty property, final IPropertyValueFactory<P> factory) {
		return factory.createDecimalAttributeValue(property, this);
	}
	
	@Override
	public void valueToString(final Long value, final StringBuilder sb) {
		if (value != null)
			sb.append(value);
	}
	
	@Override
	public TextAlignment getTextAlignment() {
		return TextAlignment.RIGHT;
	}
}
