package de.algorythm.jdoe.ui.jfx.model.meta;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class FXProperty extends FXAbstractLabeledElement {

	private final ObjectProperty<IFXPropertyType> type = new SimpleObjectProperty<>();
	private final BooleanProperty searchable = new SimpleBooleanProperty();
	private final BooleanProperty containment = new SimpleBooleanProperty();
	
	public ObjectProperty<IFXPropertyType> typeProperty() {
		return type;
	}
	
	public IFXPropertyType getType() {
		return type.get();
	}
	
	public void setType(final IFXPropertyType type) {
		this.type.set(type);
	}
	
	public BooleanProperty searchableProperty() {
		return searchable;
	}
	
	public Boolean isSearchable() {
		return searchable.get();
	}
	
	public void setSearchable(final Boolean searchable) {
		this.searchable.set(searchable);
	}
	
	public BooleanProperty containmentProperty() {
		return containment;
	}
	
	public Boolean isContainment() {
		return containment.get();
	}
	
	public void setContainment(final Boolean containment) {
		this.containment.set(containment);
	}
}