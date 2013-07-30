package de.algorythm.jdoe.model.entity.impl;

import java.util.Collection;
import java.util.LinkedList;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class Associations extends AbstractPropertyValue<Collection<IEntity>> {

	static private final long serialVersionUID = -2756229969621046760L;
	
	public Associations(final Property property) {
		super(property);
		value = new LinkedList<>();
	}

	@Override
	public void doWithValue(final IPropertyValueVisitor visitor) {
		visitor.doWithAssociations(this);
	}
	
	@Override
	public String toString() {
		return String.valueOf(value.size());
	}
}
