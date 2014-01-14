package de.algorythm.jdoe.model.entity.impl.propertyValue;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.MProperty;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;

public class StringAttributeValue extends AbstractAttributeValue<String> {

	static private final long serialVersionUID = -1261304974779213110L;

	public StringAttributeValue(final MProperty property, final AbstractAttributeType<String> type) {
		super(property, type);
	}
	
	@Override
	public void visit(IPropertyValueVisitor<IEntityReference> visitor) {
		visitor.doWithString(this);
	}

}
