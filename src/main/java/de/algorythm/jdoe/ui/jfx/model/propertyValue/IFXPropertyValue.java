package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import javafx.beans.property.ReadOnlyStringProperty;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.IFXPropertyValueChangeHandler;

public interface IFXPropertyValue<V> extends IPropertyValue<V,FXEntityReference>, Comparable<IFXPropertyValue<V>> {

	ReadOnlyStringProperty labelProperty();
	IFXPropertyValue<V> copy();
	void visit(IFXPropertyValueVisitor visitor);
	void setChangeHandler(IFXPropertyValueChangeHandler changeHandler);
}
