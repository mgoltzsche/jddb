package de.algorythm.jddb.model.entity.impl.propertyValue;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValueVisitor;
import de.algorythm.jddb.model.meta.MProperty;
import de.algorythm.jddb.model.meta.propertyTypes.AbstractAttributeType;

public class DecimalAttributeValue extends AbstractAttributeValue<Long> {

	static private final long serialVersionUID = -1383215817103797675L;

	public DecimalAttributeValue(final MProperty property, final AbstractAttributeType<Long> type) {
		super(property, type);
	}
	
	@Override
	public void visit(IPropertyValueVisitor<IEntityReference> visitor) {
		visitor.doWithDecimal(this);
	}

}
