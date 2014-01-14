package de.algorythm.jdoe.model.entity.impl.propertyValue;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.MProperty;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;

public class TextAttributeValue extends AbstractAttributeValue<String> {

	static private final long serialVersionUID = 287224901723817127L;

	public TextAttributeValue(final MProperty property, final AbstractAttributeType<String> type) {
		super(property, type);
	}
	
	@Override
	public void visit(IPropertyValueVisitor<IEntityReference> visitor) {
		visitor.doWithText(this);
	}

}
