package de.algorythm.jdoe.model.meta;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueFactory;

public interface IPropertyType<V> {
	
	String getLabel();
	boolean isUserDefined();
	<REF extends IEntityReference, P extends IPropertyValue<?,REF>> P createPropertyValue(Property property, IPropertyValueFactory<REF,P> factory);
	boolean isConform(IPropertyType<?> type);
}
