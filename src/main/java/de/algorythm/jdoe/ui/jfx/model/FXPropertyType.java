package de.algorythm.jdoe.ui.jfx.model;

import de.algorythm.jdoe.model.meta.IPropertyType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FXPropertyType<V extends IPropertyType> {

	protected final V businessModel;
	protected final SimpleStringProperty labelProperty;
	
	public FXPropertyType(final V businessModel) {
		this.businessModel = businessModel;
		labelProperty = new SimpleStringProperty(businessModel.getLabel());
	}
	
	public V getBusinessModel() {
		return businessModel;
	}
	
	public StringProperty labelProperty() {
		return labelProperty;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((businessModel == null) ? 0 : businessModel.hashCode());
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
		FXPropertyType<?> other = (FXPropertyType<?>) obj;
		if (businessModel == null) {
			if (other.businessModel != null)
				return false;
		} else if (!businessModel.equals(other.businessModel))
			return false;
		return true;
	}
}
