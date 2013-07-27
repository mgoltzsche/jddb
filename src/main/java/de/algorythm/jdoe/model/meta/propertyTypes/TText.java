package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.impl.TextValue;
import de.algorythm.jdoe.model.meta.Property;

public class TText extends AbstractAttributeType  {

	static private final long serialVersionUID = 8799483774046996115L;

	public TText() {
		super("text");
	}
	
	@Override
	public IPropertyValue createPropertyValue(final Property property) {
		return new TextValue(property);
	}
}
