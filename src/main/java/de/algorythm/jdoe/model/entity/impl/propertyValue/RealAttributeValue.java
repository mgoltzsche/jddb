package de.algorythm.jdoe.model.entity.impl.propertyValue;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.MProperty;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;

public class RealAttributeValue extends AbstractAttributeValue<Double> {

	static private final long serialVersionUID = -7312110904691647945L;

	public RealAttributeValue(final MProperty property, final AbstractAttributeType<Double> type) {
		super(property, type);
	}
	
	@Override
	public void visit(IPropertyValueVisitor<IEntityReference> visitor) {
		visitor.doWithReal(this);
	}

}
