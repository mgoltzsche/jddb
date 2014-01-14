package de.algorythm.jdoe.model.entity.impl.propertyValue;

import java.util.Date;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.MProperty;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;

public class DateAttributeValue extends AbstractAttributeValue<Date> {

	static private final long serialVersionUID = 7989679963518854455L;

	public DateAttributeValue(final MProperty property, final AbstractAttributeType<Date> type) {
		super(property, type);
	}
	
	@Override
	public void visit(IPropertyValueVisitor<IEntityReference> visitor) {
		visitor.doWithDate(this);
	}

}
