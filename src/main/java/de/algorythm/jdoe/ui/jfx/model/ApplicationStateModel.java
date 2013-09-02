package de.algorythm.jdoe.ui.jfx.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ApplicationStateModel {

	private final BooleanProperty dbClosed = new SimpleBooleanProperty(true);
	
	public Boolean isDbClosed() {
		return dbClosed.get();
	}
	
	public void setDbClosed(final Boolean closed) {
		dbClosed.set(closed);
	}
	
	public ReadOnlyBooleanProperty dbClosedProperty() {
		return dbClosed;
	}
}
