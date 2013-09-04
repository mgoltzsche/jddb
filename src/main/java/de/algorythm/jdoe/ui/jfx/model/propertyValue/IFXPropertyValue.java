package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import java.util.Map;

import javafx.beans.property.ReadOnlyStringProperty;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.IFXPropertyValueChangeHandler;

public interface IFXPropertyValue<V> extends IPropertyValue<V,FXEntityReference> {

	ReadOnlyStringProperty labelProperty();
	IFXPropertyValue<V> copy(Map<String,FXEntityReference> copiedEntities);
	void doWithValue(IFXPropertyValueVisitor visitor);
	void setChangeHandler(IFXPropertyValueChangeHandler changeHandler);
}
