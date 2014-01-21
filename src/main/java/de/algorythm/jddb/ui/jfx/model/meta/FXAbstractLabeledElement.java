package de.algorythm.jddb.ui.jfx.model.meta;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FXAbstractLabeledElement {
	
	static private int internalIdCounter = 0;

	private int internalId = ++internalIdCounter;
	protected final StringProperty label = new SimpleStringProperty();
	
	public StringProperty labelProperty() {
		return label;
	}
	
	public String getLabel() {
		return label.get();
	}
	
	public void setLabel(final String label) {
		this.label.set(label);
	}

	@Override
	public String toString() {
		return label.get();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + internalId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FXAbstractLabeledElement other = (FXAbstractLabeledElement) obj;
		if (internalId != other.internalId)
			return false;
		return true;
	}
}
