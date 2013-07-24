package de.algorythm.jdoe.model.meta.attributeTypes;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.visitor.IPropertyValueVisitor;

public class TBoolean extends AbstractAttributeType  {

	static private final long serialVersionUID = 248266786995111398L;

	public TBoolean() {
		super("boolean");
	}
	
	@Override
	public void doWithPropertyValue(IPropertyValue propertyValue,
			IPropertyValueVisitor visitor) {
		final Object valueObj = propertyValue.getValue();
		boolean value = false;
		
		if (valueObj != null) {
			try {
				value = (boolean) valueObj;
			} catch(Exception e) {
			}
		}
		
		visitor.doWithBoolean(propertyValue, value);
	}
}
