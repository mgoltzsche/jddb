package de.algorythm.jdoe.model.entity.impl;

import java.util.Collection;
import java.util.LinkedList;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.MEntityType;

public class Entity extends AbstractEntity<IPropertyValue<?,IEntityReference>,IEntityReference> {

	static private final long serialVersionUID = -4116231309999192319L;
	
	
	private Collection<IEntityReference> referringEntities;
	
	public Entity(final MEntityType type) {
		super(type);
		referringEntities = new LinkedList<IEntityReference>();
	}
	
	public Entity(final String id, final MEntityType type) {
		super(id, type);
		referringEntities = new LinkedList<IEntityReference>();
	}
	
	@Override
	public Collection<IEntityReference> getReferringEntities() {
		return referringEntities;
	}
	
	@Override
	public void setReferringEntities(final Collection<IEntityReference> referringEntities) {
		this.referringEntities = referringEntities;
	}
}