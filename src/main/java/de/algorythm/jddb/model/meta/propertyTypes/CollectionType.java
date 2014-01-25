package de.algorythm.jddb.model.meta.propertyTypes;

import java.io.Serializable;
import java.util.Collection;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.entity.IPropertyValueFactory;
import de.algorythm.jddb.model.meta.IPropertyType;
import de.algorythm.jddb.model.meta.MEntityType;
import de.algorythm.jddb.model.meta.MProperty;
import de.algorythm.jddb.model.meta.TextAlignment;

public class CollectionType implements IPropertyType<Collection<? extends IEntityReference>>, Serializable {

	static private final long serialVersionUID = 5746102308534615947L;
	static private final String NAME_SUFFIX = "[]";
	static private final String LABEL_SUFFIX = " (Liste)";
	
	private MEntityType itemType;

	public CollectionType(final MEntityType itemType) {
		this.itemType = itemType;
	}
	
	@Override
	public String getName() {
		return itemType.getName() + NAME_SUFFIX;
	}
	
	@Override
	public String getLabel() {
		return itemType.getLabel() + LABEL_SUFFIX;
	}

	@Override
	public boolean isUserDefined() {
		return Boolean.TRUE;
	}

	public MEntityType getItemType() {
		return itemType;
	}

	public void setItemType(MEntityType itemType) {
		this.itemType = itemType;
	}

	@Override
	public <P extends IPropertyValue<?,? extends IEntityReference>> P createPropertyValue(final MProperty property, final IPropertyValueFactory<P> factory) {
		return factory.createAssociationsValue(property);
	}
	
	@Override
	public boolean isConform(final IPropertyType<?> type) {
		return itemType.isConform(type);
	}
	
	@Override
	public TextAlignment getTextAlignment() {
		return TextAlignment.RIGHT;
	}
	
	@Override
	public int hashCode() {
		return 31 + ((itemType == null) ? 0 : itemType.hashCode());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CollectionType other = (CollectionType) obj;
		if (itemType == null) {
			if (other.itemType != null)
				return false;
		} else if (!itemType.equals(other.itemType))
			return false;
		return true;
	}
}
