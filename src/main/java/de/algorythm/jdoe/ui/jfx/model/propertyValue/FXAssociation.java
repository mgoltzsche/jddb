package de.algorythm.jdoe.ui.jfx.model.propertyValue;

import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public class FXAssociation extends AbstractFXPropertyValue<FXEntityReference> {

	static private final long serialVersionUID = -6560312410138927130L;

	public FXAssociation(final Property property) {
		super(property);
	}

	@Override
	public void doWithValue(IPropertyValueVisitor<FXEntityReference> visitor) {
		visitor.doWithAssociation(this);
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(value.labelProperty().get());
	}
}
