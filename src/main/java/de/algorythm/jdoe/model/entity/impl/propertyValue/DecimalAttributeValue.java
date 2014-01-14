package de.algorythm.jdoe.model.entity.impl.propertyValue;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.MProperty;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;

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
