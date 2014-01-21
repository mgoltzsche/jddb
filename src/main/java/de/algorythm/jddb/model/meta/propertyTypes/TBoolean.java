package de.algorythm.jddb.model.meta.propertyTypes;

import de.algorythm.jddb.bundle.Bundle;
import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.entity.IPropertyValueFactory;
import de.algorythm.jddb.model.meta.MProperty;

public class TBoolean extends AbstractAttributeType<Boolean> {

	static private final long serialVersionUID = 248266786995111398L;

	public TBoolean() {
		super("boolean");
	}
	
	@Override
	public <P extends IPropertyValue<?,? extends IEntityReference>> P createPropertyValue(final MProperty property, final IPropertyValueFactory<P> factory) {
		return factory.createBooleanAttributeValue(property, this);
	}
	
	@Override
	public void valueToString(final Boolean value, final StringBuilder sb) {
		sb.append(value ? Bundle.getInstance().yes : Bundle.getInstance().no);
	}
}
