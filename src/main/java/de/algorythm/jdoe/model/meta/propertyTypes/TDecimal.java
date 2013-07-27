package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.impl.DecimalValue;
import de.algorythm.jdoe.model.meta.Property;

public class TDecimal extends AbstractAttributeType  {

	static private final long serialVersionUID = -4545233017943271599L;

	public TDecimal() {
		super("decimal");
	}
	
	@Override
	public IPropertyValue createPropertyValue(final Property property) {
		return new DecimalValue(property);
	}
}
