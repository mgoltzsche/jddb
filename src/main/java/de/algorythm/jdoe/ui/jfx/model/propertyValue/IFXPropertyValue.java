package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import javafx.beans.property.ReadOnlyStringProperty;

public interface IFXPropertyValue<V> extends IPropertyValue<V,FXEntityReference> {

	ReadOnlyStringProperty labelProperty();
}
