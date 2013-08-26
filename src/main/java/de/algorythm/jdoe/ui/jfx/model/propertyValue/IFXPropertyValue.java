package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import javafx.beans.property.ReadOnlyStringProperty;

public interface IFXPropertyValue<V> extends IPropertyValue<V,FXEntityReference> {

	ReadOnlyStringProperty labelProperty();
	IFXPropertyValue<V> copy();
	void doWithValue(IFXPropertyValueVisitor visitor);
	void registerOwner(FXEntity owner);
}
