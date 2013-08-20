package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import java.util.Collection;

import javafx.collections.ObservableList;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.entity.impl.propertyValue.AbstractPropertyValue;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.IFXPropertyValueVisitor;

public class FXAssociations extends AbstractPropertyValue<Collection<FXEntityReference>> implements IFXPropertyValue<ObservableList<FXEntityReference>> {

	static private final long serialVersionUID = -2428408831904938958L;

	public FXAssociations(Property property) {
		super(property);
	}
	
	@Override
	public void doWithValue(final IPropertyValueVisitor visitor) {
		visitor.doWithAssociations(this);
	}
	
	public void doWithValue(final IFXPropertyValueVisitor visitor) {
		visitor.doWithAssociations(this);
	}

	@Override
	public void toString(final StringBuilder sb) {
		sb.append(String.valueOf(value.size()));
	}

	@Override
	protected boolean valueChanged(final ObservableList<FXEntityReference> oldValue,
			final ObservableList<FXEntityReference> newValue) {
		return !oldValue.containsAll(newValue) || !newValue.containsAll(oldValue);
	}
}
