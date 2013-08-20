package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import de.algorythm.jdoe.model.entity.impl.propertyValue.AbstractPropertyValue;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public abstract class AbstractFXPropertyValue<V> extends AbstractPropertyValue<V,FXEntityReference> implements IFXPropertyValue<V> {

	static private final long serialVersionUID = -8869779848370884103L;
	static private final String EMPTY = "";
	
	private final SimpleStringProperty label = new SimpleStringProperty(EMPTY);
	
	public AbstractFXPropertyValue(final Property property) {
		super(property);
	}
	
	@Override
	public void setValue(final V value) {
		super.setValue(value);
		
		final StringBuilder sb = new StringBuilder();
		
		toString(sb);
		
		label.set(sb.toString());
	}
	
	public ReadOnlyStringProperty labelProperty() {
		return label;
	}
}
