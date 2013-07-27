package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.impl.RealValue;
import de.algorythm.jdoe.model.meta.Property;

public class TReal extends AbstractAttributeType  {

	static private final long serialVersionUID = 4068458404531019807L;

	public TReal() {
		super("real");
	}
	
	@Override
	public IPropertyValue createPropertyValue(final Property property) {
		return new RealValue(property);
	}
}
