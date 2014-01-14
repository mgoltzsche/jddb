package de.algorythm.jdoe.ui.jfx.model.meta;

import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FXEntityType extends FXAbstractLabeledElement implements IFXPropertyType {

	private final BooleanProperty embedded = new SimpleBooleanProperty();
	private final ObservableList<FXProperty> properties = FXCollections.observableList(new LinkedList<FXProperty>());

	public BooleanProperty embeddedProperty() {
		return embedded;
	}
	
	public Boolean isEmbedded() {
		return embedded.get();
	}
	
	public void setEmbedded(final Boolean embedded) {
		this.embedded.set(embedded);
	}
	
	public ObservableList<FXProperty> getPropertiesProperty() {
		return properties;
	}
	
	public List<FXProperty> getProperties() {
		return properties;
	}
	
	@Override
	public boolean isUserDefined() {
		return Boolean.TRUE;
	}
}
