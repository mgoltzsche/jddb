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
}
