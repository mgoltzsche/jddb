package de.algorythm.jddb.ui.jfx.model.factory;

import java.util.HashSet;
import java.util.Set;

import de.algorythm.jddb.ui.jfx.model.FXEntity;
import de.algorythm.jddb.ui.jfx.model.FXEntityReference;
import de.algorythm.jddb.ui.jfx.model.propertyValue.BooleanFXAttributeValue;
import de.algorythm.jddb.ui.jfx.model.propertyValue.DateFXAttributeValue;
import de.algorythm.jddb.ui.jfx.model.propertyValue.DecimalFXAttributeValue;
import de.algorythm.jddb.ui.jfx.model.propertyValue.FXAssociation;
import de.algorythm.jddb.ui.jfx.model.propertyValue.FXAssociations;
import de.algorythm.jddb.ui.jfx.model.propertyValue.FileFXAttributeValue;
import de.algorythm.jddb.ui.jfx.model.propertyValue.IFXPropertyValue;
import de.algorythm.jddb.ui.jfx.model.propertyValue.IFXPropertyValueVisitor;
import de.algorythm.jddb.ui.jfx.model.propertyValue.RealFXAttributeValue;
import de.algorythm.jddb.ui.jfx.model.propertyValue.StringFXAttributeValue;
import de.algorythm.jddb.ui.jfx.model.propertyValue.TextFXAttributeValue;

public class AssociationCollectingVisitor implements IFXPropertyValueVisitor {

	static public final Set<FXEntityReference> associations(final FXEntity entity) {
		final Set<FXEntityReference> result = new HashSet<FXEntityReference>();
		final AssociationCollectingVisitor visitor = new AssociationCollectingVisitor(result);
		
		for (IFXPropertyValue<?> value : entity.getValues())
			value.visit(visitor);
		
		return result;
	}
	
	
	private final Set<FXEntityReference> entityRefs;
	
	private AssociationCollectingVisitor(final Set<FXEntityReference> entityRefs) {
		this.entityRefs = entityRefs;
	}
	
	@Override
	public void doWithAssociation(final FXAssociation propertyValue) {
		final FXEntityReference entityRef = propertyValue.getValue();
		
		if (entityRef != null)
			entityRefs.add(entityRef);
	}

	@Override
	public void doWithAssociations(final FXAssociations propertyValue) {
		entityRefs.addAll(propertyValue.getValue());
	}
	
	@Override
	public void doWithBoolean(BooleanFXAttributeValue propertyValue) {}

	@Override
	public void doWithDecimal(DecimalFXAttributeValue propertyValue) {}

	@Override
	public void doWithReal(RealFXAttributeValue propertyValue) {}

	@Override
	public void doWithDate(DateFXAttributeValue propertyValue) {}

	@Override
	public void doWithString(StringFXAttributeValue propertyValue) {}

	@Override
	public void doWithText(TextFXAttributeValue propertyValue) {}
	
	@Override
	public void doWithFile(FileFXAttributeValue propertyValue) {}
}
