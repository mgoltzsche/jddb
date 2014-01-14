package de.algorythm.jdoe.ui.jfx.model.meta;

import de.algorythm.jdoe.bundle.Bundle;

public class FXCollectionType extends FXAbstractLabeledElement implements IFXPropertyType {

	private final FXEntityType itemType;
	
	public FXCollectionType(final FXEntityType itemType) {
		this.itemType = itemType;
		label.bind(itemType.labelProperty().concat(" (" + Bundle.getInstance().dataSet + ")"));
	}
	
	public FXEntityType getItemType() {
		return itemType;
	}
	
	@Override
	public boolean isUserDefined() {
		return Boolean.TRUE;
	}
}
