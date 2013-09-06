package de.algorythm.jdoe.ui.jfx.model;

import de.algorythm.jdoe.ui.jfx.model.propertyValue.AbstractFXPropertyValue;

public interface IFXPropertyValueChangeHandler {

	static public final IFXPropertyValueChangeHandler DEFAULT = new IFXPropertyValueChangeHandler() {
		
		@Override
		public <V> void changeValue(final AbstractFXPropertyValue<V> propertyValue, final V value) {
			propertyValue.setPristineValue(value);
		}
		
		@Override
		public void valueChanged() {}
		
		@Override
		public void updateValueBinding(final AbstractFXPropertyValue<?> propertyValue) {
			propertyValue.unbind();
		}
	};
	
	<V> void changeValue(AbstractFXPropertyValue<V> propertyValue, V value);
	void updateValueBinding(AbstractFXPropertyValue<?> propertyValue);
	void valueChanged();
}
