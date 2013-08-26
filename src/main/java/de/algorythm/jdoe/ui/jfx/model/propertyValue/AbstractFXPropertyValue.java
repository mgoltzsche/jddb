package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import de.algorythm.jdoe.model.entity.IChangedSetter;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;

public abstract class AbstractFXPropertyValue<V> implements IFXPropertyValue<V>, IChangedSetter {

	static private final long serialVersionUID = -8869779848370884103L;
	static private final String EMPTY = "";
	
	private final Property property;
	private final SimpleStringProperty label = new SimpleStringProperty(EMPTY);
	private boolean changed;
	private FXEntity owner;
	
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
	
	@Override
	public void setChanged(final boolean changed) {
		this.changed = changed;
		
		if (changed)
			owner.applyLabelValue();
	}

	@Override
	public ReadOnlyStringProperty labelProperty() {
		return label;
	}
	
	@Override
	public void registerOwner(final FXEntity owner) {
		this.owner = owner;
	}
	
	protected void applyLabelValue() {
		final StringBuilder sb = new StringBuilder();
		
		toString(sb);
		
		label.set(sb.toString());
	}
}
