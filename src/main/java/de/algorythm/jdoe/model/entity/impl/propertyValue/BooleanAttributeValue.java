package de.algorythm.jdoe.model.entity.impl.propertyValue;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.MProperty;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;

public class BooleanAttributeValue extends AbstractAttributeValue<Boolean> {

	static private final long serialVersionUID = -6871493994878122027L;

	public BooleanAttributeValue(final MProperty property, final AbstractAttributeType<Boolean> type) {
		super(property, type);
	}
	
	@Override
	public void visit(IPropertyValueVisitor<IEntityReference> visitor) {
		visitor.doWithBoolean(this);
	}

}
