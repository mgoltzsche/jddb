package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.entity.IAttributeValueVisitor;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class TString extends AbstractAttributeType<String> {

	static private final long serialVersionUID = 6086451869372645461L;

	public TString() {
		super("string");
	}
	
	protected TString(final String label) {
		super(label);
	}
	
	@Override
	public void visit(final IPropertyValue<String,?> value,
			final IAttributeValueVisitor visitor) {
		visitor.doWithString(value);
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
