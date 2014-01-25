package de.algorythm.jddb.model.meta.propertyTypes;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.entity.IPropertyValueFactory;
import de.algorythm.jddb.model.meta.MProperty;
import de.algorythm.jddb.model.meta.TextAlignment;

public class TReal extends AbstractAttributeType<Double> {

	static private final long serialVersionUID = 4068458404531019807L;
	static private TReal instance;
	
	static public AbstractAttributeType<?> getInstance() {
		if (instance == null)
			instance = new TReal();
		
		return instance;
	}
	
	private TReal() {
		super("real", "Real number");
	}

	@Override
	public <P extends IPropertyValue<?,? extends IEntityReference>> P createPropertyValue(final MProperty property, final IPropertyValueFactory<P> factory) {
		return factory.createRealAttributeValue(property, this);
	}
	
	@Override
	public void valueToString(final Double value, final StringBuilder sb) {
		if (value != null)
			sb.append(String.format("%.2f", value));
	}
	
	@Override
	public TextAlignment getTextAlignment() {
		return TextAlignment.RIGHT;
	}
}