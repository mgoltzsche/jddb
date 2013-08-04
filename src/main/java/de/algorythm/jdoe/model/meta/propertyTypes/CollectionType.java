package de.algorythm.jdoe.model.meta.propertyTypes;

import java.io.Serializable;
import java.util.Collection;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.impl.Associations;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.IPropertyType;
import de.algorythm.jdoe.model.meta.Property;

public class CollectionType implements IPropertyType, Serializable {

	static private final long serialVersionUID = 5746102308534615947L;
	static private final String LABEL_SUFFIX = " (Liste)";

	private EntityType itemType;
	
	public CollectionType() {}
	
	public CollectionType(final EntityType itemType) {
		this.itemType = itemType;
	}
	
	@Override
	public String getLabel() {
		return itemType.getLabel() + LABEL_SUFFIX;
	}

	@Override
	public boolean isUserDefined() {
		return true;
	}

	public EntityType getItemType() {
		return itemType;
	}

	public void setItemType(EntityType itemType) {
		this.itemType = itemType;
	}

	@Override
	public IPropertyValue<Collection<IEntity>> createPropertyValue(final Property property) {
		return new Associations(property);
	}
	
	@Override
	public boolean isConform(final IPropertyType type) {
		return itemType.isConform(type);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((itemType == null) ? 0 : itemType.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
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