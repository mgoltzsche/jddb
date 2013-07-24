package de.algorythm.jdoe.model.meta.attributeTypes;

import java.util.Collection;
import java.util.LinkedList;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.IPropertyType;
import de.algorythm.jdoe.model.meta.visitor.PropertyVisitorContext;

public abstract class AbstractAttributeType extends PropertyVisitorContext implements IPropertyType {

	static private final long serialVersionUID = 4590467257394701843L;
	
	static public final Collection<AbstractAttributeType> ATTRIBUTE_TYPES = new LinkedList<>();
	
	static {
		ATTRIBUTE_TYPES.add(new TBoolean());
		ATTRIBUTE_TYPES.add(new TDecimal());
		ATTRIBUTE_TYPES.add(new TReal());
		ATTRIBUTE_TYPES.add(new TDate());
		ATTRIBUTE_TYPES.add(new TString());
		ATTRIBUTE_TYPES.add(new TText());
	}
	
	private final String name;
	
	public AbstractAttributeType(final String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isUserDefined() {
		return false;
	}
	
	@Override
	public String toString(IPropertyValue propertyValue) {
		final Object value = propertyValue.getValue();
		
		return value == null ? null : value.toString();
	}
	
	@Override
	public String toString() {
		return name;
	}
}
