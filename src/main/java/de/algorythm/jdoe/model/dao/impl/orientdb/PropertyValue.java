package de.algorythm.jdoe.model.dao.impl.orientdb;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.Property;

public class PropertyValue implements IPropertyValue {

	static private final long serialVersionUID = 3601500282325296848L;
	
	private Property property;
	private Object value;
	private transient boolean changed;

	public PropertyValue(final Property property) {
		this.property = property;
	}
	
	@Override
	public Property getProperty() {
		return property;
	}
	
	@Override
	public Object getValue() {
		return value;
	}
	
	@Override
	public void setValue(Object value) {
		if (value != this.value)
			changed = true;
		
		this.value = value;
	}
	
	@Override
	public boolean isChanged() {
		return changed;
	}
	
	public void setChanged(final boolean changed) {
		this.changed = changed;
	}
	
	@Override
	public String toString() {
		return getProperty().toString(this);
	}
}
