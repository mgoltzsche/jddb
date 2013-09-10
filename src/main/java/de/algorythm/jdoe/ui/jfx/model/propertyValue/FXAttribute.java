package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public class FXAttribute<V extends Comparable<V>> extends AbstractFXPropertyValue<V> implements ChangeListener<V> {

	static private final long serialVersionUID = 4112630308772125334L;
	
	
	private final AbstractAttributeType<V> type;
	private final SimpleObjectProperty<V> observableValue = new SimpleObjectProperty<>();
	
	public FXAttribute(final Property property, final AbstractAttributeType<V> type) {
		super(property);
		this.type = type;
	}
	
	@Override
	public void visit(final IPropertyValueVisitor<FXEntityReference> visitor) {
		type.visit(this, visitor);
	}
	
	@Override
	public void visit(final IFXPropertyValueVisitor visitor) {
		type.visit(this, visitor);
	}
	
	public ObjectProperty<V> valueProperty() {
		return observableValue;
	}
	
	@Override
	public void toString(final StringBuilder sb) {
		type.valueToString(getValue(), sb);
	}

	@Override
	public IFXPropertyValue<V> copy() {
		final FXAttribute<V> copy = new FXAttribute<>(getProperty(), type);
		
		copy.setValue(getValue());
		
		return copy;
	}
	
	@Override
	public void changed(final ObservableValue<? extends V> valueContainer, V oldValue, V newValue) {
		onObservableValueChanged(newValue);
	}
	
	@Override
	public void setObservableValue(final V value) {
		observableValue.set(value);
	}
	
	@Override
	protected void removeValueListener() {
		observableValue.removeListener(this);
	}

	@Override
	protected void addValueListener() {
		observableValue.addListener(this);
	}
	
	@Override
	public int compareTo(final IFXPropertyValue<V> propertyValue) {
		return type.compare(getValue(), propertyValue.getValue());
	}
}