package de.algorythm.jdoe.ui.jfx.model.propertyValue;

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
	public void doWithValue(IPropertyValueVisitor<FXEntityReference> visitor) {
		visitor.doWithAssociation(this);
	}
	
	@Override
	public void doWithObservableValue(IFXPropertyValueVisitor visitor) {
		visitor.doWithAssociation(getProperty(), observableValue);
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

	@Override
	public void changed(ObservableValue<? extends FXEntityReference> refContainer,
			FXEntityReference oldRef, FXEntityReference newRef) {
		setChanged(true);
		applyLabelValue();
	}
	
	public ObservableValue<FXEntityReference> observableValueProperty() {
		return observableValue;
	}
}
