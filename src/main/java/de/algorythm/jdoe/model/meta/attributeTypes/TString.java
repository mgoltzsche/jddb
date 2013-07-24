package de.algorythm.jdoe.model.meta.attributeTypes;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.visitor.IPropertyValueVisitor;

public class TString extends AbstractAttributeType  {

	static private final long serialVersionUID = 6086451869372645461L;

	public TString() {
		super("string");
	}
	
	@Override
	public void doWithPropertyValue(IPropertyValue propertyValue,
			IPropertyValueVisitor visitor) {
		final Object valueObj = propertyValue.getValue();
		String value = valueObj == null ? null : valueObj.toString();
		
		visitor.doWithString(propertyValue, value);
	}
}
