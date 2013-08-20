package de.algorythm.jdoe.ui.jfx.model.meta;

import javafx.beans.property.StringProperty;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.ILabeledElement;

public class FXType extends FXPropertyType<EntityType> implements ILabeledElement {

	public FXType(final EntityType type) {
		super(type);
	}
	
	@Override
	public String getLabel() {
		return businessModel.getLabel();
	}
	
	@Override
	public void setLabel(final String label) {
		businessModel.setLabel(label);
		labelProperty.setValue(label);
	}
	
	public StringProperty labelProperty() {
		return labelProperty;
	}
}
