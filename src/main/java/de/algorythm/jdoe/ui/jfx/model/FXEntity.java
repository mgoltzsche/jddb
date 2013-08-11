package de.algorythm.jdoe.ui.jfx.model;

import java.util.ArrayList;
import java.util.Collection;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.EntityType;

public class FXEntity {
	
	private final IEntity model;
	private final ArrayList<FXPropertyValue> values;
	private final SimpleStringProperty label = new SimpleStringProperty();
	
	public FXEntity(final IEntity model) {
		this.model = model;
		
		final Collection<IPropertyValue<?>> modelValues = model.getValues();
		
		values = new ArrayList<FXPropertyValue>(modelValues.size());
		
		for (IPropertyValue<?> value : modelValues)
			values.add(new FXPropertyValue(value));
		
		applyLabelValue();
	}
	
	public IEntity getModel() {
		return model;
	}
	
	public void applyPropertyValues() {
		for (FXPropertyValue value : values)
			value.apply();
		
		applyLabelValue();
	}
	
	private void applyLabelValue() {
		final StringBuilder sb = new StringBuilder(model.toString());
		
		if (sb.length() == 0) {
			sb.append(model.getType().getLabel());
		
			if (!model.isPersisted())
				sb.append(" (neu)");
		}
		
		final String modelLabel = sb.toString();
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				label.setValue(modelLabel);
			}
		});
	}
	
	public String getId() {
		return model.getId();
	}
	
	public EntityType getType() {
		return model.getType();
	}
	
	public boolean isPersisted() {
		return model.isPersisted();
	}
	
	public Iterable<FXPropertyValue> getValues() {
		return values;
	}
	
	public FXPropertyValue getValue(int index) {
		return values.get(index);
	}
	
	public StringProperty getLabel() {
		return label;
	}
	
	@Override
	public String toString() {
		return model.toString();
	}
	
	@Override
	public int hashCode() {
		return model.hashCode();
	}
	
	@Override
	public boolean equals(final Object o) {
		return model.equals(o);
	}
}
