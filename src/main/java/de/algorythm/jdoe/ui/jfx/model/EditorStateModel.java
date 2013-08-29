package de.algorythm.jdoe.ui.jfx.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class EditorStateModel {

	private final BooleanProperty busy = new SimpleBooleanProperty(false);
	private final BooleanProperty changed = new SimpleBooleanProperty(false);
	private final BooleanProperty pristine = new SimpleBooleanProperty(true);

	public EditorStateModel() {
		pristine.bind(changed.not());
	}
	
	public Boolean isBusy() {
		return busy.get();
	}

	public void setBusy(Boolean disable) {
		this.busy.set(disable);
	}
	
	public BooleanProperty busyProperty() {
		return busy;
	}
	
	public Boolean isChanged() {
		return changed.get();
	}
	
	public void setChanged(final Boolean changed) {
		this.changed.set(changed);
	}
	
	public BooleanProperty changedProperty() {
		return changed;
	}
	
	public ReadOnlyBooleanProperty pristineProperty() {
		return pristine;
	}
	
	public Boolean isPristine() {
		return pristine.get();
	}
}
