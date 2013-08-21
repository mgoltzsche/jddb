package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public class FXAttribute<V> extends AbstractFXPropertyValue<V> {

	static private final long serialVersionUID = 4112630308772125334L;
	
	private final AbstractAttributeType<V> type;
	
	public FXAttribute(final Property property, final AbstractAttributeType<V> type) {
		super(property);
		this.type = type;
	}
	
	@Override
	public void doWithValue(final IPropertyValueVisitor<FXEntityReference> visitor) {
		type.doWithPropertyValue(this, visitor);
	}

	@Override
	public void toString(final StringBuilder sb) {
		type.valueToString(getValue(), sb);
	}

	@Override
	public IFXPropertyValue<V> copy() {
		final FXAttribute<V> copy = new FXAttribute<>(getProperty(), type);
		
		copy.setValue(getValue());
		
		return copy;
	}
}