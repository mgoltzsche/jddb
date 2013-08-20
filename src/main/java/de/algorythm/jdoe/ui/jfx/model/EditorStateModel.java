package de.algorythm.jdoe.ui.jfx.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class EditorStateModel {

	private BooleanProperty busy = new SimpleBooleanProperty();

	public Boolean isBusy() {
		return busy.get();
	}

	public void setBusy(Boolean disable) {
		this.busy.set(disable);
	}
	
	public BooleanProperty busyProperty() {
		return busy;
	}
}
