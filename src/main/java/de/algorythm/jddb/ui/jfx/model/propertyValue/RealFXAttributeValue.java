package de.algorythm.jddb.ui.jfx.model.propertyValue;

import de.algorythm.jddb.model.entity.IPropertyValueVisitor;
import de.algorythm.jddb.model.meta.MProperty;
import de.algorythm.jddb.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jddb.ui.jfx.model.FXEntityReference;

public class RealFXAttributeValue extends AbstractFXAttributeValue<Double> {

	static private final long serialVersionUID = 8488065168023194790L;

	public RealFXAttributeValue(final MProperty property, final AbstractAttributeType<Double> type) {
		super(property, type);
	}
	
	@Override
	public void visit(final IPropertyValueVisitor<FXEntityReference> visitor) {
		visitor.doWithReal(this);
	}
	
	@Override
	public void visit(final IFXPropertyValueVisitor visitor) {
		visitor.doWithReal(this);
	}
	
	@Override
	protected AbstractFXAttributeValue<Double> createCopyInstance() {
		return new RealFXAttributeValue(getProperty(), type);
	}
}
