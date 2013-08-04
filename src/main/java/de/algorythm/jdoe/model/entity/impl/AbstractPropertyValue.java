package de.algorythm.jdoe.model.entity.impl;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.Property;

public abstract class AbstractPropertyValue<V> implements IPropertyValue<V> {

	static private final long serialVersionUID = 3601500282325296848L;
	
	protected V value;
	private Property property;
	protected transient boolean changed;

	public AbstractPropertyValue(final Property property) {
		this.property = property;
	}
	
	@Override
	public Property getProperty() {
		return property;
	}
	
	@Override
	public boolean isChanged() {
		return changed;
	}
	
	public void setChanged(final boolean changed) {
		this.changed = changed;
	}
	
	@Override
	public V getValue() {
		return value;
	}
	
	@Override
	public void setValue(final V value) {
		this.value = value;
	}
}