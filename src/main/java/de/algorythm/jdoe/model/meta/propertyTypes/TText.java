package de.algorythm.jdoe.model.meta.propertyTypes;

import de.algorythm.jdoe.model.entity.IAttributeValueVisitor;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class TText extends TString {

	static private final long serialVersionUID = 8799483774046996115L;

	public TText() {
		super("text");
	}
	
	@Override
	public void visit(final IPropertyValue<String,?> value,
			final IAttributeValueVisitor visitor) {
		visitor.doWithText(value);
	}
}
