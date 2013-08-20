package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import de.algorythm.jdoe.model.entity.impl.propertyValue.AttributePropertyValue;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;

public class FXAttributeValue<V> extends AttributePropertyValue<V> {

	static private final long serialVersionUID = 4112630308772125334L;

	private SimpleStringProperty label;
	
	public FXAttributeValue(final Property property, final AbstractAttributeType<V> type) {
		super(property, type);
	}
	
	@Override
	public void setValue(final V value) {
		super.setValue(value);
		label.set(toString());
	}
	
	public ReadOnlyStringProperty labelProperty() {
		return label;
	}
}