package de.algorythm.jdoe.model.meta.visitor;

import java.util.Collection;
import java.util.Date;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;


public interface IPropertyValueVisitor {

	void doWithEntity(IPropertyValue propertyValue, IEntity value);
	void doWithEntityCollection(IPropertyValue propertyValue, Collection<IEntity> values);
	void doWithBoolean(IPropertyValue propertyValue, boolean value);
	void doWithDecimal(IPropertyValue propertyValue, Long value);
	void doWithReal(IPropertyValue propertyValue, Double value);
	void doWithDate(IPropertyValue propertyValue, Date value);
	void doWithString(IPropertyValue propertyValue, String value);
	void doWithText(IPropertyValue propertyValue, String value);
}
