package de.algorythm.jddb.ui.jfx.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import de.algorythm.jddb.model.entity.impl.AbstractEntity;
import de.algorythm.jddb.model.meta.MEntityType;
import de.algorythm.jddb.ui.jfx.model.propertyValue.AbstractFXPropertyValue;
import de.algorythm.jddb.ui.jfx.model.propertyValue.IFXPropertyValue;
import de.algorythm.jddb.ui.jfx.model.propertyValue.IFXPropertyValueVisitor;

public class FXEntity extends AbstractEntity<IFXPropertyValue<?>, FXEntityReference> implements FXEntityReference, IFXPropertyValueChangeHandler {

	static private final long serialVersionUID = -5386143358866304236L;
	static private final String EMPTY = "";
	
	private final transient SimpleStringProperty label = new SimpleStringProperty(EMPTY);
	private transient SimpleListProperty<FXEntityReference> referringEntities = new SimpleListProperty<>(FXCollections.observableList(new LinkedList<FXEntityReference>()));
	private boolean reference;
	private IFXEntityChangeListener changeListener = IFXEntityChangeListener.DEFAULT;
	
	public FXEntity(final MEntityType type) {
		super(type);
	}
	
	public FXEntity(final String id, final MEntityType type, final boolean reference) {
		super(id, type);
		this.reference = reference;
	}
	
	public FXEntity(final FXEntity source) {
		this(source.getId(), source.getType(), source.reference);
		
		setValues(copiedPropertyValues(source));
		referringEntities.setAll(source.referringEntities);
		bindValues();
	}
	
	private ArrayList<IFXPropertyValue<?>> copiedPropertyValues(final FXEntity source) {
		final Collection<IFXPropertyValue<?>> values = source.getValues();
		final int count = values.size();
		final ArrayList<IFXPropertyValue<?>> copiedValues = new ArrayList<>(count);
		
		for (IFXPropertyValue<?> propertyValue : values)
			copiedValues.add(propertyValue.copy());
		
		return copiedValues;
	}
	
	public boolean isReference() {
		return reference;
	}
	
	public void setReference(final boolean reference) {
		this.reference = reference;
	}
	
	public void setChangeListener(final IFXEntityChangeListener changeListener) {
		this.changeListener = changeListener;
	}
	
	@Override
	public Collection<FXEntityReference> getReferringEntities() {
		return referringEntities.get();
	}
	
	@Override
	public void setReferringEntities(final Collection<FXEntityReference> referringEntities) {
		this.referringEntities.setAll(referringEntities);
	}
	
	public ListProperty<FXEntityReference> referringEntitiesProperty() {
		return referringEntities;
	}
	
	public FXEntity copy() {
		return new FXEntity(this);
	}
	
	public IFXPropertyValue<?> getValue(int index) {
		return getValues().get(index);
	}
	
	public ReadOnlyStringProperty labelProperty() {
		return label;
	}
	
	@Override
	public void visit(IFXPropertyValueVisitor propertyVisitor) {
		for (IFXPropertyValue<?> value : getValues())
			value.visit(propertyVisitor);
	}
	
	@Override
	public void assign(final FXEntityReference entityRef) {
		assign((FXEntity) entityRef);
	}
	
	public void assign(final FXEntity entity) {
		if (!getId().equals(entity.getId()))
				throw new IllegalArgumentException("Given entity has a different id");
		
		final Iterator<IFXPropertyValue<?>> iter = getValues().iterator();
		final Iterator<IFXPropertyValue<?>> otherIter = entity.getValues().iterator();
		
		while (iter.hasNext())
			assignPropertyValue(iter.next(), otherIter.next());
		
		bindValues();
	}
	
	private <V> void assignPropertyValue(final IFXPropertyValue<V> propertyValue, IFXPropertyValue<?> other) {
		@SuppressWarnings("unchecked")
		final IFXPropertyValue<V> otherValue = (IFXPropertyValue<V>) other;
		
		propertyValue.setChangeHandler(IFXPropertyValueChangeHandler.DEFAULT);
		propertyValue.setValue(otherValue.getValue());
	}
	
	public void bindValues() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (IFXPropertyValue<?> value : getValues())
					value.setChangeHandler(FXEntity.this);
				
				updateLabelValue();
			}
		});
	}
	
	private void updateLabelValue() {
		final StringBuilder sb = new StringBuilder();
		
		for (IFXPropertyValue<?> value : getValues()) {
			if (!value.getProperty().getType().isUserDefined()) { // attrs only
				String valueStr = value.labelProperty().get().replaceAll("[\\s]+", " ");
				
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
		propertyValue.setObservableValue(value);
	}
	
	@Override
	public void valueChanged() {
		changeListener.changed();
		updateLabelValue();
	}
	
	@Override
	public void updateValueBinding(final AbstractFXPropertyValue<?> propertyValue) {
		propertyValue.bind();
	}
	
	@Override
	public void toString(final StringBuilder sb) {
		sb.append(label.get());
	}
	
	@Override
	public String toString() {
		return label.get();
	}
}
