package de.algorythm.jdoe.model.entity.impl.propertyValue;

import java.util.Collection;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.MProperty;

public class Associations extends AbstractPropertyValue<Collection<IEntityReference>> {

	static private final long serialVersionUID = -2428408831904938958L;

	public Associations(MProperty property) {
		super(property);
	}
	
	@Override
	public void visit(final IPropertyValueVisitor<IEntityReference> visitor) {
		visitor.doWithAssociations(this);
	}

	@Override
	public void toString(final StringBuilder sb) {
		sb.append(String.valueOf(getValue().size()));
	}
}
