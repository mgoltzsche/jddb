package de.algorythm.jdoe.model.meta.propertyTypes;

import java.io.Serializable;

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
	public IPropertyValue createPropertyValue(final Property property) {
		return new Associations(property);
	}
}
