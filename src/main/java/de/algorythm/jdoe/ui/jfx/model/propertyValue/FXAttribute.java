package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import java.util.Map;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public class FXAttribute<V> extends AbstractFXPropertyValue<V> implements ChangeListener<V> {

	static private final long serialVersionUID = 4112630308772125334L;
	
	
	private final AbstractAttributeType<V> type;
	private final SimpleObjectProperty<V> observableValue = new SimpleObjectProperty<>();
	
	public FXAttribute(final Property property, final AbstractAttributeType<V> type) {
		super(property);
		this.type = type;
		observableValue.addListener(this);
	}
	
	@Override
	public void visit(final IPropertyValueVisitor<FXEntityReference> visitor) {
		type.visit(this, visitor);
	}
	
	@Override
	public void doWithValue(final IFXPropertyValueVisitor visitor) {
		type.visit(this, visitor);
	}

	@Override
	public void toString(final StringBuilder sb) {
		type.valueToString(value, sb);
	}

	@Override
	public IFXPropertyValue<V> copy(final Map<String, FXEntityReference> copiedEntities) {
		final FXAttribute<V> copy = new FXAttribute<>(getProperty(), type);
		
		copy.setValue(value);
		copy.pristine = pristine;
		
		return copy;
	}
	
	@Override
	public void changed(final ObservableValue<? extends V> valueContainer, V oldValue, V newValue) {
		value = newValue;
		changed();
	}

	@Override
	protected void updateObservableValueInternal() {
		observableValue.removeListener(this);
		observableValue.set(value);
		observableValue.addListener(this);
	}
	
	@Override
	public void setNewValue(final V value) {
		observableValue.set(value);
	}
}