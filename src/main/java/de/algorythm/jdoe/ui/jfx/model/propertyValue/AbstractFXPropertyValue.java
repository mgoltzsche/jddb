package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import de.algorythm.jdoe.model.entity.IPropertyValueChangeHandler;
import de.algorythm.jdoe.model.entity.impl.DefaultPropertyValueChangeHandler;
import de.algorythm.jdoe.model.meta.Property;

public abstract class AbstractFXPropertyValue<V> implements IFXPropertyValue<V> {
	
	static private final long serialVersionUID = -8869779848370884103L;
	static private final String EMPTY = "";
	
	private final Property property;
	private final SimpleStringProperty label = new SimpleStringProperty(EMPTY);
	private boolean changed;
	private IPropertyValueChangeHandler changeHandler = DefaultPropertyValueChangeHandler.INSTANCE;
	
	public AbstractFXPropertyValue(final Property property) {
		this.property = property;
	}
	
	@Override
	public Property getProperty() {
		return property;
	}
	
	@Override
	public boolean isChanged() {
		return changed;
	}
	
	protected void setChanged(final boolean changed) {
		this.changed = changeHandler.changed(changed);
	}

	protected void changed() {
		applyLabelValue();
		setChanged(true);
	}
	
	@Override
	public ReadOnlyStringProperty labelProperty() {
		return label;
	}
	
	@Override
	public void setChangeHandler(final IPropertyValueChangeHandler changeHandler) {
		this.changeHandler = changeHandler;
	}
	
	protected void applyLabelValue() {
		final StringBuilder sb = new StringBuilder();
		
		toString(sb);
		
		label.set(sb.toString());
	}
	
	@Override
	public String toString() {
		return label.get();
	}
}
