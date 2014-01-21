package de.algorythm.jddb.model.entity.impl.propertyValue;

import java.util.Date;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValueVisitor;
import de.algorythm.jddb.model.meta.MProperty;
import de.algorythm.jddb.model.meta.propertyTypes.AbstractAttributeType;

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
