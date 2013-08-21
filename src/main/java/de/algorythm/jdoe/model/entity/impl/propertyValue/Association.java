package de.algorythm.jdoe.model.entity.impl.propertyValue;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.Property;

public class Association extends AbstractPropertyValue<IEntityReference,IEntityReference> {

	static private final long serialVersionUID = 6390396248168541378L;

	public Association(final Property property) {
		super(property);
	}
	
	@Override
	public void doWithValue(final IPropertyValueVisitor<IEntityReference> visitor) {
		visitor.doWithAssociation(this);
	}

	@Override
	public void toString(final StringBuilder sb) {
		final IEntityReference ref = getValue();
		
		if (ref != null)
			ref.toString(sb);
	}

}
