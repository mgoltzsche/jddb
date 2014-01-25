package de.algorythm.jddb.model.meta.propertyTypes;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.entity.IPropertyValueFactory;
import de.algorythm.jddb.model.meta.MProperty;

public class TString extends AbstractAttributeType<String> {

	static private final long serialVersionUID = 6086451869372645461L;
	static private TString instance;
	
	static public AbstractAttributeType<?> getInstance() {
		if (instance == null)
			instance = new TString();
		
		return instance;
	}
	
	protected TString() {
		super("string", "Single-line text");
	}
	
	protected TString(final String name, final String label) {
		super(name, label);
	}
	
	@Override
	public <P extends IPropertyValue<?,? extends IEntityReference>> P createPropertyValue(final MProperty property, final IPropertyValueFactory<P> factory) {
		return factory.createStringAttributeValue(property, this);
	}
	
	@Override
	public void valueToString(final String value, final StringBuilder sb) {
		if (value != null)
			sb.append(value);
	}
	
	@Override
	public int compare(final String a, final String b) {
		return a == null && b == null ? 0 : a == null ? -1 : b == null ? 1 : a.compareToIgnoreCase(b);
	}
}
