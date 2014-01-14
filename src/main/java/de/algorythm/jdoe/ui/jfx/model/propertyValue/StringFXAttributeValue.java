package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.MProperty;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public class StringFXAttributeValue extends AbstractFXAttributeValue<String> {

	static private final long serialVersionUID = -2112397596733304610L;

	public StringFXAttributeValue(final MProperty property, final AbstractAttributeType<String> type) {
		super(property, type);
	}
	
	@Override
	public void visit(final IPropertyValueVisitor<FXEntityReference> visitor) {
		visitor.doWithString(this);
	}
	
	@Override
	public void visit(final IFXPropertyValueVisitor visitor) {
		visitor.doWithString(this);
	}
	
	@Override
	protected AbstractFXAttributeValue<String> createCopyInstance() {
		return new StringFXAttributeValue(getProperty(), type);
	}
}
