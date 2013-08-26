package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import de.algorythm.jdoe.model.entity.IAttributeValueVisitor;

public interface IFXPropertyValueVisitor extends IAttributeValueVisitor {
	
	void doWithAssociation(FXAssociation propertyValue);
	void doWithAssociations(FXAssociations propertyValue);
}