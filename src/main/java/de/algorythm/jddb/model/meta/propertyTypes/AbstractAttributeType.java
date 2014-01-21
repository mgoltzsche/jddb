package de.algorythm.jddb.model.meta.propertyTypes;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

import de.algorythm.jddb.model.meta.IPropertyType;
import de.algorythm.jddb.model.meta.TextAlignment;

public abstract class AbstractAttributeType<V extends Comparable<V>> implements IPropertyType<V>, Comparator<V>, Serializable {

	static private final long serialVersionUID = 5778323289559217617L;
	
	static public final Collection<AbstractAttributeType<?>> ATTRIBUTE_TYPES = new LinkedList<>();
	
	static {
		ATTRIBUTE_TYPES.add(new TBoolean());
		ATTRIBUTE_TYPES.add(new TDecimal());
		ATTRIBUTE_TYPES.add(new TReal());
		ATTRIBUTE_TYPES.add(new TDate());
		ATTRIBUTE_TYPES.add(new TString());
		ATTRIBUTE_TYPES.add(new TText());
		ATTRIBUTE_TYPES.add(new TFile());
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
		return Boolean.FALSE;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
	@Override
	public boolean isConform(final IPropertyType<?> type) {
		return this == type;
	}
	
	@Override
	public TextAlignment getTextAlignment() {
		return TextAlignment.LEFT;
	}
	
	@Override
	public int compare(final V a, final V b) {
		return a == null && b == null ? 0 : (a == null ? -1 : (b == null ? 1 : a.compareTo(b)));
	}

	public abstract void valueToString(V value, StringBuilder sb);
	
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
		AbstractAttributeType<?> other = (AbstractAttributeType<?>) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
}
