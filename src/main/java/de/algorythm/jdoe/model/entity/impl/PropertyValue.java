package de.algorythm.jdoe.model.entity.impl;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.IPropertyType;
import de.algorythm.jdoe.model.meta.Property;

public class PropertyValue<V> implements IPropertyValue<V> {

	static private final long serialVersionUID = 3601500282325296848L;
	static protected final String EMPTY = "";
	
	protected V value;
	private Property property;
	protected IPropertyType<V> type;
	protected transient boolean changed;

	public PropertyValue(final Property property, final IPropertyType<V> type) {
		this.property = property;
		this.type = type;
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
		if (type.valueChanged(this.value, value)) {
			this.value = value;
			changed = true;
		}
	}
	
	@Override
	public void doWithValue(final IPropertyValueVisitor visitor) {
		type.doWithPropertyValue(this, visitor);
	}
	
	@Override
	public void toString(final StringBuilder sb) {
		type.valueToString(value, sb);
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		type.valueToString(value, sb);
		return sb.toString();
	}
}
