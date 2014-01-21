package de.algorythm.jddb.ui.jfx.model.propertyValue;

import de.algorythm.jddb.model.entity.IPropertyValueVisitor;
import de.algorythm.jddb.model.meta.MProperty;
import de.algorythm.jddb.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jddb.ui.jfx.model.FXEntityReference;

public class BooleanFXAttributeValue extends AbstractFXAttributeValue<Boolean> {

	static private final long serialVersionUID = -6989345867696932235L;

	public BooleanFXAttributeValue(final MProperty property, final AbstractAttributeType<Boolean> type) {
		super(property, type);
	}
	
	@Override
	public void visit(final IPropertyValueVisitor<FXEntityReference> visitor) {
		visitor.doWithBoolean(this);
	}
	
	@Override
	public void visit(final IFXPropertyValueVisitor visitor) {
		visitor.doWithBoolean(this);
	}
	
	@Override
	protected AbstractFXAttributeValue<Boolean> createCopyInstance() {
		return new BooleanFXAttributeValue(getProperty(), type);
	}
}
