package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import java.util.Map;

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
	public void visit(final IPropertyValueVisitor<FXEntityReference> visitor) {
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
	public IFXPropertyValue<FXEntityReference> copy(final Map<String, FXEntityReference> copiedEntities) {
		final FXAssociation copy = new FXAssociation(getProperty());
		final FXEntityReference entityRef = getValue();
		
		if (entityRef != null) {
			final String id = entityRef.getId();
			final FXEntityReference copiedRef = copiedEntities.get(id);
			
			copy.setValue(copiedRef == null ? entityRef.copy(copiedEntities) : copiedRef);
		}
		
		copy.pristine = pristine;
		
		return copy;
	}
	
	public ObjectProperty<FXEntityReference> valueProperty() {
		return observableValue;
	}

	@Override
	public void changed(final ObservableValue<? extends FXEntityReference> refContainer,
			FXEntityReference oldRef, FXEntityReference newRef) {
		value = newRef;
		changed();
	}
	
	@Override
	protected void updateLabelValue() {
		if (value == null) {
			label.unbind();
			label.set(EMPTY);
		} else
			label.bind(value.labelProperty());
	}
	
	@Override
	protected void updateObservableValueInternal() {
		observableValue.removeListener(this);
		observableValue.set(value);
		observableValue.addListener(this);
	}

	@Override
	public void setNewValue(final FXEntityReference value) {
		observableValue.set(value);
	}
}