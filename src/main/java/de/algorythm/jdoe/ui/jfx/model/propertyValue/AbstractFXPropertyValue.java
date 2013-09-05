package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.ui.jfx.model.IFXPropertyValueChangeHandler;

public abstract class AbstractFXPropertyValue<V> implements IFXPropertyValue<V> {
	
	static private final long serialVersionUID = -8869779848370884103L;
	static protected final String EMPTY = "";
	
	protected V value;
	private transient final Property property;
	protected transient final SimpleStringProperty label = new SimpleStringProperty(EMPTY);
	protected transient boolean pristine = true;
	protected transient IFXPropertyValueChangeHandler changeHandler = IFXPropertyValueChangeHandler.PRISTINE;
	
	public AbstractFXPropertyValue(final Property property) {
		this.property = property;
	}
	
	@Override
	public Property getProperty() {
		return property;
	}
	
	@Override
	public boolean isPristine() {
		return pristine;
	}

	@Override
	public V getValue() {
		return value;
	}
	
	@Override
	public void setValue(final V value) {
		changeHandler.changeValue(this, value);
	}
	
	public void setPristineValue(final V value) {
		this.value = value;
	}
	
	public abstract void setObservableValue(final V value);
	
	@Override
	public void setPristine(final boolean pristine) {
		this.pristine = pristine;
	}
	
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
		pristine = false;
		updateLabelValue();
		changeHandler.valueChanged();
	}
	
	@Override
	public String toString() {
		return label.get();
	}
}
