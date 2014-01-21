package de.algorythm.jddb.model.entity;

import java.util.Date;

import de.algorythm.jddb.model.meta.MProperty;
import de.algorythm.jddb.model.meta.propertyTypes.AbstractAttributeType;

public interface IPropertyValueFactory<P extends IPropertyValue<?,? extends IEntityReference>> {

	P createAssociationValue(MProperty property);
	P createAssociationsValue(MProperty property);
	P createBooleanAttributeValue(MProperty property, AbstractAttributeType<Boolean> type);
	P createDecimalAttributeValue(MProperty property, AbstractAttributeType<Long> type);
	P createRealAttributeValue(MProperty property, AbstractAttributeType<Double> type);
	P createDateAttributeValue(MProperty property, AbstractAttributeType<Date> type);
	P createStringAttributeValue(MProperty property, AbstractAttributeType<String> type);
	P createTextAttributeValue(MProperty property, AbstractAttributeType<String> type);
	P createFileAttributeValue(MProperty property, AbstractAttributeType<String> type);
}
