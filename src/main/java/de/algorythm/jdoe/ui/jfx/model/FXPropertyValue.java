package de.algorythm.jdoe.ui.jfx.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class FXPropertyValue {
	
	private final IPropertyValue<?> model;
	private final SimpleStringProperty value = new SimpleStringProperty();
	private final BooleanProperty validProperty = new SimpleBooleanProperty(true);
	
	public FXPropertyValue(final IPropertyValue<?> model) {
		this.model = model;
		
		apply();
	}
	
	public void apply() {
		value.setValue(model.toString());
	}
	
	public IPropertyValue<?> getModel() {
		return model;
	}
	
	public StringProperty getValue() {
		return value;
	}
	
	public BooleanProperty validProperty() {
		return validProperty;
	}
}
