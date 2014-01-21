package de.algorythm.jddb.model.entity.impl.propertyValue;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValueVisitor;
import de.algorythm.jddb.model.meta.MProperty;
import de.algorythm.jddb.model.meta.propertyTypes.AbstractAttributeType;

public class FileAttributeValue extends AbstractAttributeValue<String> {

	static private final long serialVersionUID = 6902944437644566074L;

	public FileAttributeValue(final MProperty property, final AbstractAttributeType<String> type) {
		super(property, type);
	}
	
	@Override
	public void visit(IPropertyValueVisitor<IEntityReference> visitor) {
		visitor.doWithFile(this);
	}

}
