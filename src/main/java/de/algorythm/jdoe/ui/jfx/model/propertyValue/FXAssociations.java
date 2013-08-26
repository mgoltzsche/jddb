package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import java.util.ArrayList;
import java.util.Collection;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public class FXAssociations extends AbstractFXPropertyValue<Collection<FXEntityReference>> implements ListChangeListener<FXEntityReference> {

	static private final long serialVersionUID = -2428408831904938958L;
	
	
	private final ListProperty<FXEntityReference> observableValue = new SimpleListProperty<>();

	public FXAssociations(final Property property) {
		super(property);
		observableValue.addListener(this);
	}
	
	@Override
	public void doWithValue(final IPropertyValueVisitor<FXEntityReference> visitor) {
		visitor.doWithAssociations(this);
	}
	
	@Override
	public void doWithValue(final IFXPropertyValueVisitor visitor) {
		visitor.doWithAssociations(this);
	}
	
	@Override
	public void toString(final StringBuilder sb) {
		sb.append(String.valueOf(getValue().size()));
	}

	@Override
	public IFXPropertyValue<Collection<FXEntityReference>> copy() {
		final FXAssociations copy = new FXAssociations(getProperty());
		
		copy.setValue(new ArrayList<>(copy.getValue()));
		
		return copy;
	}

	@Override
	public Collection<FXEntityReference> getValue() {
		return observableValue;
	}

	@Override
	public void setValue(final Collection<FXEntityReference> value) {
		observableValue.setAll(value);
	}
	
	public ListProperty<FXEntityReference> valueProperty() {
		return observableValue;
	}

	@Override
	public void onChanged(final Change<? extends FXEntityReference> change) {
		setChanged(true);
		applyLabelValue();
	}
}
