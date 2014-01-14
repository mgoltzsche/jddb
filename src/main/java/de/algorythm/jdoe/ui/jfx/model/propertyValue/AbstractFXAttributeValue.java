package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.algorythm.jdoe.model.meta.MProperty;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;

public abstract class AbstractFXAttributeValue<V extends Comparable<V>> extends AbstractFXPropertyValue<V> implements ChangeListener<V> {

	static private final long serialVersionUID = 4112630308772125334L;
	
	
	protected final AbstractAttributeType<V> type;
	private final SimpleObjectProperty<V> observableValue = new SimpleObjectProperty<>();
	
	public AbstractFXAttributeValue(final MProperty property, final AbstractAttributeType<V> type) {
		super(property);
		this.type = type;
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
		final AbstractFXAttributeValue<V> copy = createCopyInstance();
		
		copy.setValue(getValue());
		
		return copy;
	}
	
	protected abstract AbstractFXAttributeValue<V> createCopyInstance();
	
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