package de.algorythm.jdoe.ui.jfx.model.meta;

import javafx.beans.property.ReadOnlyStringProperty;
import de.algorythm.jdoe.model.meta.MEntityType;
import de.algorythm.jdoe.model.meta.ILabeledElement;

public class FXType extends FXPropertyType<MEntityType> implements ILabeledElement {

	public FXType(final MEntityType type) {
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
	
	public ReadOnlyStringProperty labelProperty() {
		return labelProperty;
	}
	
	public boolean isEmbedded() {
		return businessModel.isEmbedded();
	}
	
	public void setEmbedded(final boolean embedded) {
		businessModel.setEmbedded(embedded);
	}
}
