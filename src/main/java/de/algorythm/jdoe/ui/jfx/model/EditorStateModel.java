package de.algorythm.jdoe.ui.jfx.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EditorStateModel {

	private final StringProperty title = new SimpleStringProperty();
	private final BooleanProperty busy = new SimpleBooleanProperty(true);
	private final BooleanProperty pristine = new SimpleBooleanProperty(true);
	
	public String getTitle() {
		return title.get();
	}
	
	public void setTitle(final String title) {
		this.title.set(title);
	}
	
	public StringProperty titleProperty() {
		return title;
	}
	
	public Boolean isBusy() {
		return busy.get();
	}

	public void setBusy(Boolean disable) {
		this.busy.set(disable);
	}
	
	public ReadOnlyBooleanProperty busyProperty() {
		return busy;
	}
	
	public Boolean isPristine() {
		return pristine.get();
	}
	
	public void setPristine(final Boolean pristine) {
		this.pristine.set(pristine);
	}
	
	public BooleanProperty pristineProperty() {
		return pristine;
	}
}
