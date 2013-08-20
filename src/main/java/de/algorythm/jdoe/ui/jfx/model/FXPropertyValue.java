package de.algorythm.jdoe.ui.jfx.model;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import de.algorythm.jdoe.model.entity.impl.PropertyValue;
import de.algorythm.jdoe.model.meta.IPropertyType;
import de.algorythm.jdoe.model.meta.Property;

public class FXPropertyValue<V> extends PropertyValue<V> {

	static private final long serialVersionUID = -8869779848370884103L;
	static private final String EMPTY = "";
	
	private final SimpleStringProperty label = new SimpleStringProperty(EMPTY);
	
	public FXPropertyValue(final Property property, final IPropertyType<V> type) {
		super(property, type);
	}
	
	@Override
	public void setValue(final V value) {
		super.setValue(value);
		
		final StringBuilder sb = new StringBuilder();
		
		type.valueToString(value, sb);
		
		label.set(sb.toString());
	}
	
	public ReadOnlyStringProperty labelProperty() {
		return label;
	}
}
