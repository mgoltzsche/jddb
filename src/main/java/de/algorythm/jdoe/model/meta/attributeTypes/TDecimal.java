package de.algorythm.jdoe.model.meta.attributeTypes;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.visitor.IPropertyValueVisitor;

public class TDecimal extends AbstractAttributeType  {

	static private final long serialVersionUID = -4545233017943271599L;

	public TDecimal() {
		super("decimal");
	}
	
	@Override
	public void doWithPropertyValue(IPropertyValue propertyValue,
			IPropertyValueVisitor visitor) {
		Long value = null;
		
		try {
			value = Long.valueOf(propertyValue.getValue().toString());
		} catch(Exception e) {}
		
		visitor.doWithDecimal(propertyValue, value);
	}
}
