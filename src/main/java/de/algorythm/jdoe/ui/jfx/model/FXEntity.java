package de.algorythm.jdoe.ui.jfx.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.impl.AbstractEntity;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.AbstractFXPropertyValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;

public class FXEntity extends AbstractEntity<IFXPropertyValue<?>, FXEntityReference> implements FXEntityReference, IFXPropertyValueChangeHandler {

	static private final long serialVersionUID = -5386143358866304236L;
	
	
	private final transient SimpleBooleanProperty pristine = new SimpleBooleanProperty(true);
	private final transient SimpleStringProperty label = new SimpleStringProperty();
	private boolean reference;
	
	public FXEntity(final EntityType type) {
		super(type);
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
		final String id = getId();
		final FXEntity copy = new FXEntity(id, getType(), reference);
		
		copiedEntities.put(id, copy);
		
		copy.setValues(copiedPropertyValues(copiedEntities));
		copy.pristine.set(pristine.get());
		copy.bindValues();
		
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
	
	public IFXPropertyValue<?> getValue(int index) {
		return values.get(index);
	}
	
	public ReadOnlyStringProperty labelProperty() {
		return label;
	}
	
	@Override
	public void assign(final FXEntityReference entityRef) {
		assign((FXEntity) entityRef);
	}
	
	public void assign(final FXEntity entity) {
		if (!getId().equals(entity.getId()))
				throw new IllegalArgumentException("Given entity has a different id");
		
		final Iterator<IFXPropertyValue<?>> iter = values.iterator();
		final Iterator<IFXPropertyValue<?>> otherIter = entity.values.iterator();
		
		while (iter.hasNext())
			assignPropertyValue(iter.next(), otherIter.next());
		
		bindValues();
	}
	
	private <V> void assignPropertyValue(final IFXPropertyValue<V> propertyValue, IFXPropertyValue<?> other) {
		@SuppressWarnings("unchecked")
		final IFXPropertyValue<V> otherValue = (IFXPropertyValue<V>) other;
		
		propertyValue.setChangeHandler(IFXPropertyValueChangeHandler.PRISTINE);
		propertyValue.setValue(otherValue.getValue());
	}
	
	public boolean isPristine() {
		return pristine.get();
	}
	
	public void setPristine(final boolean pristine) {
		this.pristine.set(pristine);
		
		for (IPropertyValue<?,FXEntityReference> value : values)
			value.setPristine(pristine);
	}
	
	public ReadOnlyBooleanProperty pristineProperty() {
		return pristine;
	}
	
	public void bindValues() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (IFXPropertyValue<?> value : values)
					value.setChangeHandler(FXEntity.this);
				
				updateLabelValue();
			}
		});
	}
	
	private void updateLabelValue() {
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
	
	@Override
	public <V> void changeValue(final AbstractFXPropertyValue<V> propertyValue, final V value) {
		propertyValue.setNewValue(value);
	}
	
	@Override
	public void valueChanged() {
		pristine.set(false);
		updateLabelValue();
	}
	
	@Override
	public void updateBoundValues(final AbstractFXPropertyValue<?> propertyValue) {
		propertyValue.updateBoundValues();
	}
	
	@Override
	public void toString(final StringBuilder sb) {
		sb.append(label.get());
	}
}
