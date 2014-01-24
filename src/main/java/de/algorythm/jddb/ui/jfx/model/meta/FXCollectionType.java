package de.algorythm.jddb.ui.jfx.model.meta;

import de.algorythm.jddb.bundle.Bundle;

public class FXCollectionType extends FXAbstractLabeledElement implements IFXPropertyType {

	private final FXEntityType itemType;
	
	public FXCollectionType(final FXEntityType itemType) {
		this.itemType = itemType;
		labelProperty.bind(itemType.labelProperty().concat(" (" + Bundle.getInstance().dataSet + ")"));
	}
	
	public FXEntityType getItemType() {
		return itemType;
	}
	
	@Override
	public Boolean isUserDefined() {
		return itemType.isUserDefined();
	}
	
	@Override
	public Boolean isEmbedded() {
		return itemType.isEmbedded();
	}
}
