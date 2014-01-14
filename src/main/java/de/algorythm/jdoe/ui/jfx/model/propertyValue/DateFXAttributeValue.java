package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import java.util.Date;

import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.MProperty;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public class DateFXAttributeValue extends AbstractFXAttributeValue<Date> {

	static private final long serialVersionUID = 3874775302747672193L;

	public DateFXAttributeValue(final MProperty property, final AbstractAttributeType<Date> type) {
		super(property, type);
	}
	
	@Override
	public void visit(final IPropertyValueVisitor<FXEntityReference> visitor) {
		visitor.doWithDate(this);
	}
	
	@Override
	public void visit(final IFXPropertyValueVisitor visitor) {
		visitor.doWithDate(this);
	}
	
	@Override
	protected AbstractFXAttributeValue<Date> createCopyInstance() {
		return new DateFXAttributeValue(getProperty(), type);
	}
}
