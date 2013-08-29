package de.algorythm.jdoe.model.entity.impl;

import java.util.Collection;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.EntityType;

public class Entity extends AbstractEntity<IPropertyValue<?,IEntityReference>,IEntityReference> {

	static private final long serialVersionUID = -4116231309999192319L;
	
	
	public Entity(final EntityType type) {
		super(type);
	}
	
	public Entity(final String id, final EntityType type, final Collection<IEntityReference> referencingEntities) {
		super(id, type, referencingEntities);
	}
	
	public Entity(final String id, final EntityType type) {
		super(id, type);
	}
}