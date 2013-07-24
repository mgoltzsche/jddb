package de.algorythm.jdoe.model.meta.attributeTypes;

import java.util.Date;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.visitor.IPropertyValueVisitor;

public class TDate extends AbstractAttributeType  {

	static private final long serialVersionUID = -7251553097102848742L;

	public TDate() {
		super("date");
	}
	
	@Override
	public void doWithPropertyValue(IPropertyValue propertyValue,
			IPropertyValueVisitor visitor) {
		Date value = null;
		
		try {
			value = new Date(Long.valueOf(propertyValue.getValue().toString()));
		} catch(Exception e) {}
		
		visitor.doWithDate(propertyValue, value);
	}
}
