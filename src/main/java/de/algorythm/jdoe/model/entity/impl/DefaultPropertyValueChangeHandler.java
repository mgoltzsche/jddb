package de.algorythm.jdoe.model.entity.impl;

import de.algorythm.jdoe.model.entity.IPropertyValueChangeHandler;

public class DefaultPropertyValueChangeHandler implements IPropertyValueChangeHandler {

	static public final DefaultPropertyValueChangeHandler INSTANCE = new DefaultPropertyValueChangeHandler();
	
	
	private DefaultPropertyValueChangeHandler() {}
	
	@Override
	public boolean changed() {
		return false;
	}

}
