package de.algorythm.jdoe.ui.jfx.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class EditorStateModel {

	private BooleanProperty disable = new SimpleBooleanProperty();

	public Boolean getDisable() {
		return disable.get();
	}

	public void setDisable(Boolean disable) {
		this.disable.set(disable);
	}
	
	public BooleanProperty disableProperty() {
		return disable;
	}
}
