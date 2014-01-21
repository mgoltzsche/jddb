package de.algorythm.jddb.ui.jfx.model.propertyValue;

import de.algorythm.jddb.model.entity.IPropertyValueVisitor;
import de.algorythm.jddb.model.meta.MProperty;
import de.algorythm.jddb.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jddb.ui.jfx.model.FXEntityReference;

public class TextFXAttributeValue extends AbstractFXAttributeValue<String> {

	static private final long serialVersionUID = -2416967994835754102L;

	public TextFXAttributeValue(final MProperty property, final AbstractAttributeType<String> type) {
		super(property, type);
	}
	
	@Override
	public void visit(final IPropertyValueVisitor<FXEntityReference> visitor) {
		visitor.doWithText(this);
	}
	
	@Override
	public void visit(final IFXPropertyValueVisitor visitor) {
		visitor.doWithText(this);
	}
	
	@Override
	protected AbstractFXAttributeValue<String> createCopyInstance() {
		return new TextFXAttributeValue(getProperty(), type);
	}
}
