package de.algorythm.jddb.model.meta.propertyTypes;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.entity.IPropertyValueFactory;
import de.algorythm.jddb.model.meta.MProperty;

public class TText extends TString {

	static private final long serialVersionUID = 8799483774046996115L;
	static private TText instance;
	
	static public AbstractAttributeType<?> getInstance() {
		if (instance == null)
			instance = new TText();
		
		return instance;
	}
	
	private TText() {
		super("text", "Multiline text");
	}
	
	@Override
	public <P extends IPropertyValue<?,? extends IEntityReference>> P createPropertyValue(final MProperty property, final IPropertyValueFactory<P> factory) {
		return factory.createTextAttributeValue(property, this);
	}
}
