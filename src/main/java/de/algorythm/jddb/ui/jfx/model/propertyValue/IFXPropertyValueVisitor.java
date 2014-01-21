package de.algorythm.jddb.ui.jfx.model.propertyValue;

public interface IFXPropertyValueVisitor {
	
	void doWithAssociation(FXAssociation propertyValue);
	void doWithAssociations(FXAssociations propertyValue);
	void doWithBoolean(BooleanFXAttributeValue propertyValue);
	void doWithDecimal(DecimalFXAttributeValue propertyValue);
	void doWithReal(RealFXAttributeValue propertyValue);
	void doWithDate(DateFXAttributeValue propertyValue);
	void doWithString(StringFXAttributeValue propertyValue);
	void doWithText(TextFXAttributeValue propertyValue);
	void doWithFile(FileFXAttributeValue propertyValue);
}