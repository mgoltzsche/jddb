package de.algorythm.jddb.ui.jfx.model.propertyValue;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import de.algorythm.jddb.model.meta.MProperty;
import de.algorythm.jddb.ui.jfx.model.IFXPropertyValueChangeHandler;

public abstract class AbstractFXPropertyValue<V> implements IFXPropertyValue<V>, Comparable<IFXPropertyValue<V>> {
	
	static private final long serialVersionUID = -8869779848370884103L;
	static protected final String EMPTY = "";
	
	private V value;
	private final MProperty property;
	protected transient final SimpleStringProperty label = new SimpleStringProperty(EMPTY);
	protected transient IFXPropertyValueChangeHandler changeHandler = IFXPropertyValueChangeHandler.DEFAULT;
	
	public AbstractFXPropertyValue(final MProperty property) {
		this.property = property;
	}
	
	@Override
	public MProperty getProperty() {
		return property;
	}

	@Override
	public V getValue() {
		return value;
	}
	
	@Override
	public void setValue(final V value) {
		this.value = value;
		changeHandler.changeValue(this, value);
	}
	
	public abstract void setObservableValue(final V value);
	
	@Override
	public ReadOnlyStringProperty labelProperty() {
		return label;
	}
	
	@Override
	public void setChangeHandler(final IFXPropertyValueChangeHandler changeHandler) {
		this.changeHandler = changeHandler;
		
		changeHandler.updateValueBinding(this);
	}
	
	public void unbind() {
		removeValueListener();
	}
	
	public void bind() {
		setObservableValue(value);
		addValueListener();
		updateLabelValue();
	}
	
	protected abstract void removeValueListener();
	
	protected abstract void addValueListener();
	
	protected void updateLabelValue() {
		final StringBuilder sb = new StringBuilder();
		
		toString(sb);
		
		label.set(sb.toString());
	}
	
	protected void onObservableValueChanged(final V value) {
		this.value = value;
		updateLabelValue();
		changeHandler.valueChanged();
	}
	
	@Override
	public String toString() {
		return label.get();
	}
}
