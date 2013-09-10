package de.algorythm.jdoe.model.meta;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueFactory;

public interface IPropertyType<V> {
	
	String getLabel();
	boolean isUserDefined();
	<P extends IPropertyValue<?,REF>, REF extends IEntityReference> P createPropertyValue(Property property, IPropertyValueFactory<P,REF> factory);
	boolean isConform(IPropertyType<?> type);
	TextAlignment getTextAlignment();
}
