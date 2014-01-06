package de.algorythm.jdoe.model.meta.propertyTypes;

import java.io.File;

import de.algorythm.jdoe.model.entity.IAttributeValueVisitor;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class TFile extends TString {

	static private final long serialVersionUID = 6086451869372645461L;

	public TFile() {
		super("file");
	}
	
	@Override
	public void visit(final IPropertyValue<String,?> value,
			final IAttributeValueVisitor visitor) {
		visitor.doWithFile(value);
	}
	
	@Override
	public void valueToString(final String value, final StringBuilder sb) {
		if (value != null)
			sb.append(new File(value).getName());
	}
}
