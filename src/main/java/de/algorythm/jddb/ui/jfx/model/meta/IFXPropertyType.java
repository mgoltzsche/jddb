package de.algorythm.jddb.ui.jfx.model.meta;

import javafx.beans.property.StringProperty;

public interface IFXPropertyType {
	
	StringProperty labelProperty();
	String getLabel();
	void setLabel(String label);
	Boolean isUserDefined();
	Boolean isEmbedded();
}
