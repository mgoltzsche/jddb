package de.algorythm.jddb.ui.jfx.model.meta;

import java.util.LinkedList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FXEntityType extends FXAbstractLabeledChangable implements IFXPropertyType {

	private final BooleanProperty embedded = new SimpleBooleanProperty();
	private final ObservableList<FXProperty> properties = FXCollections.observableList(new LinkedList<FXProperty>());

	public FXEntityType(final InvalidationListener invalidationListener) {
		super(invalidationListener);
		properties.addListener(invalidationListener);
	}
	
	@Override
	public String getItemLabel() {
		return getLabel();
	}
	
	public Property<Boolean> embeddedProperty() {
		return embedded;
	}
	
	@Override
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
	public Boolean isUserDefined() {
		return Boolean.TRUE;
	}
}
