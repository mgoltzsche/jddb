package de.algorythm.jdoe.ui.jfx.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.impl.AbstractEntity;
import de.algorythm.jdoe.model.entity.impl.DefaultPropertyValueChangeHandler;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;

public class FXEntity extends AbstractEntity<IFXPropertyValue<?>, FXEntityReference> implements FXEntityReference {

	static private final long serialVersionUID = -5386143358866304236L;
	
	
	private final transient SimpleBooleanProperty changed = new SimpleBooleanProperty();
	private final transient SimpleStringProperty label = new SimpleStringProperty();
	private boolean reference;
	
	public FXEntity(final EntityType type) {
		super(type);
		reference = false;
	}
	
	public FXEntity(final String id, final EntityType type, final boolean reference) {
		super(id, type);
		this.reference = reference;
	}
	
	public boolean isReference() {
		return reference;
	}
	
	public void setReference(final boolean reference) {
		this.reference = reference;
	}
	
	public FXEntity copy() {
		return copy(new HashMap<String,FXEntityReference>());
	}
	
	@Override
	public FXEntity copy(final Map<String,FXEntityReference> copiedEntities) {
		// TODO: add copy to copiedEntities before copying properties
		final String id = getId();
		final FXEntity copy = new FXEntity(id, getType(), reference);
		
		copiedEntities.put(id, copy);
		
		copy.setValues(copiedPropertyValues(copiedEntities));
		
		copy.changed.set(changed.get());
		
		return copy;
	}
	
	private ArrayList<IFXPropertyValue<?>> copiedPropertyValues(final Map<String,FXEntityReference> copiedEntities) {
		final Collection<IFXPropertyValue<?>> values = getValues();
		final int count = values.size();
		final ArrayList<IFXPropertyValue<?>> copiedValues = new ArrayList<>(count);
		
		for (IFXPropertyValue<?> propertyValue : values)
			copiedValues.add(propertyValue.copy(copiedEntities));
		
		return copiedValues;
	}
	
	@Override
	public void setValues(final ArrayList<IFXPropertyValue<?>> values) {
		super.setValues(values);
		applyLabelValue();
	}
	
	private void applyLabelValue() {
		final StringBuilder sb = new StringBuilder();
		
		for (IFXPropertyValue<?> value : values) {
			if (!value.getProperty().getType().isUserDefined()) { // attrs only
				final String valueStr = value.labelProperty().get();
				
				if (!valueStr.isEmpty()) {
					if (sb.length() > 0)
						sb.append(", ");
					
					sb.append(valueStr);
				}
			}
		}
		
		label.set(sb.length() == 0 ? getType().getLabel() : sb.toString());
	}
	
	public IFXPropertyValue<?> getValue(int index) {
		return values.get(index);
	}
	
	public ReadOnlyStringProperty labelProperty() {
		return label;
	}
	
	@Override
	public void assign(final FXEntityReference entityRef) {
		if (!getId().equals(entityRef.getId()))
			throw new IllegalArgumentException("Given entity reference has a different id");
		
		label.set(entityRef.labelProperty().get());
	}
	
	public void assign(final FXEntity entity) {
		if (!getId().equals(entity.getId()))
				throw new IllegalArgumentException("Given entity has a different id");
		
		final Iterator<IFXPropertyValue<?>> iter = values.iterator();
		final Iterator<IFXPropertyValue<?>> otherIter = entity.values.iterator();
		
		while (iter.hasNext())
			assignPropertyValue(iter.next(), otherIter.next());
		
		applyLabelValue();
	}
	
	private <V> void assignPropertyValue(final IFXPropertyValue<V> propertyValue, IFXPropertyValue<?> other) {
		@SuppressWarnings("unchecked")
		final IFXPropertyValue<V> otherValue = (IFXPropertyValue<V>) other;
		
		propertyValue.setChangeHandler(DefaultPropertyValueChangeHandler.INSTANCE);
		propertyValue.setValue(otherValue.getValue());
		propertyValue.setChangeHandler(this);
	}
	
	public boolean isChanged() {
		return changed.get();
	}
	
	public void setChanged(final boolean changed) {
		this.changed.set(changed);
		
		for (IPropertyValue<?,FXEntityReference> value : values)
			value.setChanged(changed);
	}
	
	public ReadOnlyBooleanProperty changedProperty() {
		return changed;
	}
	
	@Override
	public boolean changed() {
		changed.set(true);
		applyLabelValue();
		
		return true;
	}
	
	@Override
	public void toString(final StringBuilder sb) {
		sb.append(label.get());
	}
}
