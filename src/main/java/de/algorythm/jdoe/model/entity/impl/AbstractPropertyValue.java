package de.algorythm.jdoe.model.entity.impl;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.Property;

public abstract class AbstractPropertyValue implements IPropertyValue {

	static private final long serialVersionUID = 3601500282325296848L;
	
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
}
