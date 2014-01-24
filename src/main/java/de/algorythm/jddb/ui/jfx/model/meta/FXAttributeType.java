package de.algorythm.jddb.ui.jfx.model.meta;

import de.algorythm.jddb.model.meta.IPropertyType;

public class FXAttributeType extends FXAbstractLabeledElement implements IFXPropertyType {

	private final IPropertyType<?> type;
	
	public FXAttributeType(final IPropertyType<?> type) {
		this.type = type;
		labelProperty.set(type.getLabel());
	}
	
	public IPropertyType<?> getType() {
		return type;
	}
	
	@Override
	public Boolean isUserDefined() {
		return Boolean.FALSE;
	}

	@Override
	public Boolean isEmbedded() {
		return Boolean.FALSE;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FXAttributeType other = (FXAttributeType) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
