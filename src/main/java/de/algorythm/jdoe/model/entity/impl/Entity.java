package de.algorythm.jdoe.model.entity.impl;

import java.util.ArrayList;
import java.util.Collection;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.EntityType;

public class Entity extends AbstractEntity<IEntityReference> {

	static private final long serialVersionUID = -4116231309999192319L;
	
	public Entity(final EntityType type, final ArrayList<? extends IPropertyValue<?,IEntityReference>> values) {
		super(type, values);
	}
	
	public Entity(final String id, final EntityType type, final ArrayList<? extends IPropertyValue<?,IEntityReference>> values, final Collection<IEntityReference> referencingEntities) {
		super(id, type, values, referencingEntities);
	}
	
	public Entity(final String id, final EntityType type, final ArrayList<? extends IPropertyValue<?,IEntityReference>> values) {
		super(id, type, values);
	}
}