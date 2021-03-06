package de.algorythm.jddb.ui.jfx.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EditorStateModel {

	private final StringProperty title = new SimpleStringProperty();
	private final StringProperty typeName = new SimpleStringProperty();
	private final BooleanProperty busy = new SimpleBooleanProperty(true);
	private final BooleanProperty creating = new SimpleBooleanProperty(true);
	private final BooleanProperty pristine = new SimpleBooleanProperty(true);
	private final BooleanProperty deleting = new SimpleBooleanProperty(false);
	
	public String getTitle() {
		return title.get();
	}
	
	public void setTitle(final String title) {
		this.title.set(title);
	}
	
	public StringProperty titleProperty() {
		return title;
	}
	
	public String getTypeName() {
		return typeName.get();
	}
	
	public void setTypeName(final String typeName) {
		this.typeName.set(typeName);
	}
	
	public StringProperty typeNameProperty() {
		return typeName;
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
	
	public Boolean isCreating() {
		return creating.get();
	}
	
	public void setCreating(final Boolean creating) {
		this.creating.set(creating);
	}
	
	public BooleanProperty creatingProperty() {
		return creating;
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
	
	public Boolean isDeleting() {
		return deleting.get();
	}
	
	public void setDeleting(final Boolean deleting) {
		this.deleting.set(deleting);
	}
	
	public BooleanProperty deletingProperty() {
		return deleting;
	}
}
