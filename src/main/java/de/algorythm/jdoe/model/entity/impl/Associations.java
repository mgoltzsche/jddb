package de.algorythm.jdoe.model.entity.impl;

import java.util.Collection;
import java.util.LinkedList;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class Associations extends AbstractPropertyValue {

	static private final long serialVersionUID = -2756229969621046760L;
	
	private Collection<IEntity> value = new LinkedList<>();
	
	public Associations(final Property property) {
		super(property);
	}
	
	public Collection<IEntity> getValue() {
		return value;
	}
	
	public void setValue(final Collection<IEntity> value) {
		this.value = value;
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
