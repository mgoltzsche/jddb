package de.algorythm.jdoe.model.entity;

import de.algorythm.jdoe.model.entity.impl.Association;
import de.algorythm.jdoe.model.entity.impl.Associations;
import de.algorythm.jdoe.model.entity.impl.BooleanValue;
import de.algorythm.jdoe.model.entity.impl.DateValue;
import de.algorythm.jdoe.model.entity.impl.DecimalValue;
import de.algorythm.jdoe.model.entity.impl.RealValue;
import de.algorythm.jdoe.model.entity.impl.StringValue;
import de.algorythm.jdoe.model.entity.impl.TextValue;


public interface IPropertyValueVisitor {

	void doWithAssociation(Association propertyValue);
	void doWithAssociations(Associations propertyValue);
	void doWithBoolean(BooleanValue propertyValue);
	void doWithDecimal(DecimalValue propertyValue);
	void doWithReal(RealValue propertyValue);
	void doWithDate(DateValue propertyValue);
	void doWithString(StringValue propertyValue);
	void doWithText(TextValue propertyValue);
}
