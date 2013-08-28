package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public class FXAssociation extends AbstractFXPropertyValue<FXEntityReference> implements ChangeListener<FXEntityReference> {

	static private final long serialVersionUID = -6560312410138927130L;
	private final SimpleObjectProperty<FXEntityReference> observableValue = new SimpleObjectProperty<>();

	public FXAssociation(final Property property) {
		super(property);
		observableValue.addListener(this);
	}

	@Override
	public void doWithValue(final IPropertyValueVisitor<FXEntityReference> visitor) {
		visitor.doWithAssociation(this);
	}
	
	@Override
	public void doWithValue(final IFXPropertyValueVisitor visitor) {
		visitor.doWithAssociation(this);
	}

	@Override
	public void toString(final StringBuilder sb) {
		final FXEntityReference ref = getValue();
		
		if (ref != null)
			sb.append(ref.labelProperty().get());
	}
	
	@Override
	public IFXPropertyValue<FXEntityReference> copy() {
		final FXAssociation copy = new FXAssociation(getProperty());
		
		copy.setValue(getValue());
		copy.changed = changed;
		
		return copy;
	}

	@Override
	public FXEntityReference getValue() {
		return observableValue.get();
	}

	@Override
	public void setValue(final FXEntityReference value) {
		observableValue.set(value);
	}
	
	public ObjectProperty<FXEntityReference> valueProperty() {
		return observableValue;
	}

	@Override
	public void changed(final ObservableValue<? extends FXEntityReference> refContainer,
			FXEntityReference oldRef, FXEntityReference newRef) {
		changed();
	}
	
	public ObservableValue<FXEntityReference> observableValueProperty() {
		return observableValue;
	}
}
