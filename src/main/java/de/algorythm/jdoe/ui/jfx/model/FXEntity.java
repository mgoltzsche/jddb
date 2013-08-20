package de.algorythm.jdoe.ui.jfx.model;

import java.util.ArrayList;
import java.util.Collection;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import de.algorythm.jdoe.model.entity.impl.AbstractEntity;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;

public class FXEntity extends AbstractEntity<FXEntityReference> implements FXEntityReference {

	static private final long serialVersionUID = -5386143358866304236L;
	
	private final ArrayList<IFXPropertyValue<?>> values;
	private final SimpleStringProperty label = new SimpleStringProperty();
	
	public FXEntity(final EntityType type, final ArrayList<IFXPropertyValue<?>> values) {
		super(type, values);
		this.values = values;
		
		applyLabelValue();
	}
	
	public FXEntity(final String id, final EntityType type, final ArrayList<IFXPropertyValue<?>> values) {
		super(id, type, values);
		this.values = values;
		
		applyLabelValue();
	}
	
	public FXEntity(final String id, final EntityType type, final ArrayList<IFXPropertyValue<?>> values, final Collection<FXEntityReference> referringEntities) {
		super(id, type, values, referringEntities);
		this.values = values;
		
		applyLabelValue();
	}
	
	private void applyLabelValue() {
		final StringBuilder sb = new StringBuilder();
		
		for (IFXPropertyValue<?> value : values)
			if (value.getProperty().getType().isUserDefined())
				sb.append(value.labelProperty().get());
		
		label.setValue(sb.toString());
	}
	
	public IFXPropertyValue<?> getValue(int index) {
		return values.get(index);
	}
	
	public ReadOnlyStringProperty labelProperty() {
		return label;
	}
	
	@Override
	public void toString(final StringBuilder sb) {
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
	}
}
