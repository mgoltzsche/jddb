package de.algorythm.jddb.ui.jfx.model.meta;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import de.algorythm.jddb.bundle.Bundle;

public class FXAbstractLabeledElement {
	
	static private int internalIdCounter = 0;

	private int internalId = ++internalIdCounter;
	protected final SimpleStringProperty labelProperty = new SimpleStringProperty(Bundle.getInstance().unknown);

	public StringProperty labelProperty() {
		return labelProperty;
	}
	
	public String getLabel() {
		return labelProperty.get();
	}
	
	public void setLabel(final String label) {
		labelProperty.set(label);
	}
	
	@Override
	public String toString() {
		return labelProperty.get();
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
