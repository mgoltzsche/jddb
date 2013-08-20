package de.algorythm.jdoe.model.meta.propertyTypes;

import java.io.Serializable;
import java.util.Collection;

import de.algorythm.jdoe.model.dao.IPropertyValueFactory;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.IPropertyType;
import de.algorythm.jdoe.model.meta.Property;

public class CollectionType implements IPropertyType<Collection<IEntityReference>>, Serializable {

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
	public <E extends IEntityReference, P extends IPropertyValue<?,E>> P createPropertyValue(final Property property, final IPropertyValueFactory<E,P> factory) {
		return factory.createAssociationsValue(property);
	}
	
	@Override
	public boolean isConform(final IPropertyType<?> type) {
		return itemType.isConform(type);
	}
	
	@Override
	public int hashCode() {
		return 31 + ((itemType == null) ? 0 : itemType.hashCode());
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
