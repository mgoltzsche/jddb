package de.algorythm.jdoe.model.entity.impl;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class Association extends AbstractPropertyValue {

	static private final long serialVersionUID = -5998762071271767269L;
	
	private IEntity value;
	
	public Association(final Property property) {
		super(property);
	}
	
	public IEntity getValue() {
		return value;
	}
	
	public void setValue(final IEntity value) {
		this.value = value;
	}
	
	@Override
	public void doWithValue(final IPropertyValueVisitor visitor) {
		visitor.doWithAssociation(this);
	}
	
	@Override
	public String toString() {
		return value == null ? null : value.toString();
	}
}
