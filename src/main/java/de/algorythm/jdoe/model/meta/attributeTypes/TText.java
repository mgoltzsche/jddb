package de.algorythm.jdoe.model.meta.attributeTypes;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.visitor.IPropertyValueVisitor;

public class TText extends AbstractAttributeType  {

	static private final long serialVersionUID = 8799483774046996115L;

	public TText() {
		super("text");
	}
	
	@Override
	public void doWithPropertyValue(IPropertyValue propertyValue,
			IPropertyValueVisitor visitor) {
		final Object valueObj = propertyValue.getValue();
		String value = valueObj == null ? null : valueObj.toString();
		
		visitor.doWithText(propertyValue, value);
	}
}
