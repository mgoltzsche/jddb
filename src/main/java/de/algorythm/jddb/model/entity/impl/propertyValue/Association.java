package de.algorythm.jddb.model.entity.impl.propertyValue;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValueVisitor;
import de.algorythm.jddb.model.meta.MProperty;

public class Association extends AbstractPropertyValue<IEntityReference> {

	static private final long serialVersionUID = 6390396248168541378L;

	public Association(final MProperty property) {
		super(property);
	}
	
	@Override
	public void visit(final IPropertyValueVisitor<IEntityReference> visitor) {
		visitor.doWithAssociation(this);
	}

	@Override
	public void toString(final StringBuilder sb) {
		final IEntityReference ref = getValue();
		
		if (ref != null)
			ref.toString(sb);
	}

}
