package de.algorythm.jdoe.model.meta.propertyTypes;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import de.algorythm.jdoe.model.meta.IPropertyType;

public abstract class AbstractAttributeType implements IPropertyType, Serializable {

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
	
	private final String label;
	
	public AbstractAttributeType(final String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public boolean isUserDefined() {
		return false;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
	@Override
	public boolean isConform(final IPropertyType type) {
		return this == type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractAttributeType other = (AbstractAttributeType) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
}
