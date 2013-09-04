package de.algorythm.jdoe.ui.jfx.model;

import de.algorythm.jdoe.ui.jfx.model.propertyValue.AbstractFXPropertyValue;

public interface IFXPropertyValueChangeHandler {

	static public final IFXPropertyValueChangeHandler PRISTINE = new IFXPropertyValueChangeHandler() {
		
		@Override
		public <V> void changeValue(final AbstractFXPropertyValue<V> propertyValue, final V value) {
			propertyValue.setPristineValue(value);
		}
		
		@Override
		public void valueChanged() {}
		
		@Override
		public void updateBoundValues(final AbstractFXPropertyValue<?> propertyValue) {}
	};
	
	<V> void changeValue(AbstractFXPropertyValue<V> propertyValue, V value);
	void updateBoundValues(AbstractFXPropertyValue<?> propertyValue);
	void valueChanged();
}
