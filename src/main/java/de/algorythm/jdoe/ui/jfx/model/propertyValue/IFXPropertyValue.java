package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import javafx.beans.property.ReadOnlyStringProperty;

public interface IFXPropertyValue<V> extends IPropertyValue<V> {

	ReadOnlyStringProperty labelProperty();
}
