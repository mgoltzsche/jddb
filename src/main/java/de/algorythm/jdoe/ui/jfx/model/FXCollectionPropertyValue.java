package de.algorythm.jdoe.ui.jfx.model;

import java.util.Collection;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.entity.impl.PropertyValue;
import de.algorythm.jdoe.model.meta.IPropertyType;
import de.algorythm.jdoe.model.meta.Property;

public class FXCollectionPropertyValue extends FXPropertyValue<Collection<FXEntityReference>> {

	static private final long serialVersionUID = 388458344171067582L;

	public FXCollectionPropertyValue(final Property property,
			final IPropertyType<Collection<FXEntityReference>> type) {
		super(property, type);
	}
	
	@Override
	public void doWithValue(final IPropertyValueVisitor<FXEntityReference> visitor) {
		visitor.doWithAssociations(this);
	}
}
