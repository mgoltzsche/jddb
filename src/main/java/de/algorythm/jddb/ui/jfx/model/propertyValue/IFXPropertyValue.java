package de.algorythm.jddb.ui.jfx.model.propertyValue;

import javafx.beans.property.ReadOnlyStringProperty;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.ui.jfx.model.FXEntityReference;
import de.algorythm.jddb.ui.jfx.model.IFXPropertyValueChangeHandler;

public interface IFXPropertyValue<V> extends IPropertyValue<V,FXEntityReference> {

	ReadOnlyStringProperty labelProperty();
	IFXPropertyValue<V> copy();
	void visit(IFXPropertyValueVisitor visitor);
	void setChangeHandler(IFXPropertyValueChangeHandler changeHandler);
}
