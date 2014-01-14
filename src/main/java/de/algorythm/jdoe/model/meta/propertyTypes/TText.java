package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueFactory;
import de.algorythm.jdoe.model.meta.MProperty;

public class TText extends TString {

	static private final long serialVersionUID = 8799483774046996115L;

	public TText() {
		super("text");
	}
	
	@Override
	public <P extends IPropertyValue<?,? extends IEntityReference>> P createPropertyValue(final MProperty property, final IPropertyValueFactory<P> factory) {
		return factory.createTextAttributeValue(property, this);
	}
}
