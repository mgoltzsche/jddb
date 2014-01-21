package de.algorythm.jddb.ui.jfx.model.propertyValue;

import de.algorythm.jddb.model.entity.IPropertyValueVisitor;
import de.algorythm.jddb.model.meta.MProperty;
import de.algorythm.jddb.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jddb.ui.jfx.model.FXEntityReference;

public class FileFXAttributeValue extends AbstractFXAttributeValue<String> {

	static private final long serialVersionUID = 8553809100264015055L;

	public FileFXAttributeValue(final MProperty property, final AbstractAttributeType<String> type) {
		super(property, type);
	}
	
	@Override
	public void visit(final IPropertyValueVisitor<FXEntityReference> visitor) {
		visitor.doWithFile(this);
	}
	
	@Override
	public void visit(final IFXPropertyValueVisitor visitor) {
		visitor.doWithFile(this);
	}
	
	@Override
	protected AbstractFXAttributeValue<String> createCopyInstance() {
		return new FileFXAttributeValue(getProperty(), type);
	}
}
