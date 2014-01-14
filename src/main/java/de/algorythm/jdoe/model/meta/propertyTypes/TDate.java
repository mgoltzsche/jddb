package de.algorythm.jdoe.model.meta.propertyTypes;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueFactory;
import de.algorythm.jdoe.model.meta.MProperty;
import de.algorythm.jdoe.model.meta.TextAlignment;

public class TDate extends AbstractAttributeType<Date>  {

	static private final long serialVersionUID = -7251553097102848742L;

	public TDate() {
		super("date");
	}
	
	@Override
	public <P extends IPropertyValue<?,? extends IEntityReference>> P createPropertyValue(final MProperty property, final IPropertyValueFactory<P> factory) {
		return factory.createDateAttributeValue(property, this);
	}
	
	@Override
	public void valueToString(final Date value, final StringBuilder sb) {
		if (value != null)
			sb.append(new SimpleDateFormat().format(value));
	}
	
	@Override
	public TextAlignment getTextAlignment() {
		return TextAlignment.CENTER;
	}
}
