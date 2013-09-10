package de.algorythm.jdoe.model.entity.impl.propertyValue;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;

public class AttributePropertyValue<V extends Comparable<V>> extends AbstractPropertyValue<V,IEntityReference> {

	static private final long serialVersionUID = 2179613684367210970L;

	private final AbstractAttributeType<V> type;
	
	public AttributePropertyValue(final Property property, final AbstractAttributeType<V> type) {
		super(property);
		this.type = type;
	}
	
	@Override
	public void visit(final IPropertyValueVisitor<IEntityReference> visitor) {
		type.visit(this, visitor);
	}

	@Override
	public void toString(final StringBuilder sb) {
		type.valueToString(getValue(), sb);
	}
}
