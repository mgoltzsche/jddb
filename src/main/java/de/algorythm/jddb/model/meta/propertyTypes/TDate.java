package de.algorythm.jddb.model.meta.propertyTypes;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.entity.IPropertyValueFactory;
import de.algorythm.jddb.model.meta.MProperty;
import de.algorythm.jddb.model.meta.TextAlignment;

public class TDate extends AbstractAttributeType<Date>  {

	static private final long serialVersionUID = -7251553097102848742L;
	static private TDate instance;
	
	static public AbstractAttributeType<?> getInstance() {
		if (instance == null)
			instance = new TDate();
		
		return instance;
	}
	
	private TDate() {
		super("date", "Date");
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
