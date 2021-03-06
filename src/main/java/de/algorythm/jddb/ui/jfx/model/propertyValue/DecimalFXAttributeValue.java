package de.algorythm.jddb.ui.jfx.model.propertyValue;

import de.algorythm.jddb.model.entity.IPropertyValueVisitor;
import de.algorythm.jddb.model.meta.MProperty;
import de.algorythm.jddb.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jddb.ui.jfx.model.FXEntityReference;

public class DecimalFXAttributeValue extends AbstractFXAttributeValue<Long> {
	
	static private final long serialVersionUID = -8898094849390101333L;

	public DecimalFXAttributeValue(final MProperty property, final AbstractAttributeType<Long> type) {
		super(property, type);
	}
	
	@Override
	public void visit(final IPropertyValueVisitor<FXEntityReference> visitor) {
		visitor.doWithDecimal(this);
	}
	
	@Override
	public void visit(final IFXPropertyValueVisitor visitor) {
		visitor.doWithDecimal(this);
	}
	
	@Override
	protected AbstractFXAttributeValue<Long> createCopyInstance() {
		return new DecimalFXAttributeValue(getProperty(), type);
	}
}
