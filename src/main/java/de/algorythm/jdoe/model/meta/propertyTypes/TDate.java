package de.algorythm.jdoe.model.meta.propertyTypes;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.algorythm.jdoe.model.dao.IPropertyValueFactory;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class TDate extends AbstractAttributeType<Date>  {

	static private final long serialVersionUID = -7251553097102848742L;

	public TDate() {
		super("date");
	}
	
	@Override
	public <P extends IPropertyValue<?>> P createPropertyValue(final Property property, final IPropertyValueFactory<P> factory) {
		return factory.createPropertyValue(property, this);
	}

	@Override
	public void doWithPropertyValue(final IPropertyValue<Date> value,
			final IPropertyValueVisitor visitor) {
		visitor.doWithDate(value);
	}
	
	@Override
	public void valueToString(final Date value, final StringBuilder sb) {
		if (value != null)
			sb.append(new SimpleDateFormat().format(value));
	}
}
