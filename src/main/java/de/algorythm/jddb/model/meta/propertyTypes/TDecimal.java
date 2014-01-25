package de.algorythm.jddb.model.meta.propertyTypes;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.entity.IPropertyValueFactory;
import de.algorythm.jddb.model.meta.MProperty;
import de.algorythm.jddb.model.meta.TextAlignment;

public class TDecimal extends AbstractAttributeType<Long> {

	static private final long serialVersionUID = -4545233017943271599L;
	static private TDecimal instance;
	
	static public AbstractAttributeType<?> getInstance() {
		if (instance == null)
			instance = new TDecimal();
		
		return instance;
	}
	
	private TDecimal() {
		super("decimal", "Decimal number");
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
