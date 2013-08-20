package de.algorythm.jdoe.ui.jfx.model;

import javafx.collections.ObservableList;
import de.algorythm.jdoe.model.entity.IAttributeValueVisitor;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public interface IFXPropertyValueVisitor extends IAttributeValueVisitor {

	void doWithAssociation(IPropertyValue<FXEntityReference> propertyValue);
	void doWithAssociations(IPropertyValue<ObservableList<FXEntityReference>> propertyValue);
}
