package de.algorythm.jdoe.model.meta.attributeTypes;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.visitor.IPropertyValueVisitor;

public class TReal extends AbstractAttributeType  {

	static private final long serialVersionUID = 4068458404531019807L;

	public TReal() {
		super("real");
	}
	
	@Override
	public void doWithPropertyValue(IPropertyValue propertyValue,
			IPropertyValueVisitor visitor) {
		Double value = null;
		
		try {
			value = Double.valueOf(propertyValue.getValue().toString());
		} catch(Exception e) {}
		
		visitor.doWithReal(propertyValue, value);
	}
}
