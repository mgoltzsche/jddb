package de.algorythm.jddb.model.meta;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.entity.IPropertyValueFactory;

public interface IPropertyType<V> {
	
	String getLabel();
	boolean isUserDefined();
	<P extends IPropertyValue<?,? extends IEntityReference>> P createPropertyValue(MProperty property, IPropertyValueFactory<P> factory);
	boolean isConform(IPropertyType<?> type);
	TextAlignment getTextAlignment();
}
