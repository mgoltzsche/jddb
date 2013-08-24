package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import javafx.beans.value.ObservableObjectValue;
import javafx.collections.ObservableList;
import de.algorythm.jdoe.model.entity.IAttributeValueVisitor;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public interface IFXPropertyValueVisitor extends IAttributeValueVisitor {
	
	void doWithAssociation(Property property, ObservableObjectValue<FXEntityReference> ref);
	void doWithAssociations(Property property, ObservableList<FXEntityReference> refs);
}