package de.algorythm.jdoe.model.meta;

import de.algorythm.jdoe.model.dao.IPropertyValueFactory;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public interface IPropertyType<V> {
	
	String getLabel();
	boolean isUserDefined();
	<E extends IEntityReference, P extends IPropertyValue<?,E>> P createPropertyValue(Property property, IPropertyValueFactory<E,P> factory);
	boolean isConform(IPropertyType<?> type);
}
