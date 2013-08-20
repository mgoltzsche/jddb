package de.algorythm.jdoe.model.entity.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.EntityType;

public class Entity implements IEntity {

	static private final long serialVersionUID = -4116231309999192319L;
	
	private final String id;
	private final EntityType type;
	private final ArrayList<? extends IPropertyValue<?>> values;
	private final Collection<? extends IEntityReference> referencingEntities;
	private transient boolean transientInstance;
	
	public Entity(final EntityType type, final ArrayList<? extends IPropertyValue<?>> values) {
		this(UUID.randomUUID().toString(), type, values, new LinkedList<IEntityReference>());
		transientInstance = true;
	}
	
	public Entity(final String id, final EntityType type, final ArrayList<? extends IPropertyValue<?>> values, final Collection<? extends IEntityReference> referencingEntities) {
		this.id = id;
		this.type = type;
		this.values = values;
		this.referencingEntities = referencingEntities;
	}
	
	public Entity(final String id, final EntityType type, final ArrayList<? extends IPropertyValue<?>> values) {
		this.id = id;
		this.type = type;
		referencingEntities = null;
		this.values = values;
	}
	
	public Entity(final Entity entity) {
		id = entity.id;
		type = entity.type;
		values = new ArrayList<>(entity.values);
		referencingEntities = entity.referencingEntities;
		transientInstance = entity.transientInstance;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	public void setTransientInstance(final boolean transientInstance) {
		this.transientInstance = transientInstance;
	}
	
	@Override
	public boolean isChanged() {
		for (IPropertyValue<?> value : values)
			if (value.isChanged())
				return true;
		
		return false;
	}
	
	@Override
	public EntityType getType() {
		return type;
	}
	
	@Override
	public Collection<? extends IPropertyValue<?>> getValues() {
		return values;
	}
	
	@Override
	public Iterable<? extends IEntityReference> getReferencingEntities() {
		return referencingEntities;
	}
	
	@Override
	public boolean isTransientInstance() {
		return transientInstance;
	}
	
	@Override
	public IEntity getTransientInstance() {
		if (!transientInstance)
			throw new IllegalStateException();
		
		return this;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		toString(sb);
		
		return sb.toString();
	}
	
	@Override
	public void toString(final StringBuilder sb) {
		for (IPropertyValue<?> value : getValues()) {
			if (!value.getProperty().getType().isUserDefined()) { // attrs only
				final int lastLength = sb.length();
				
				value.toString(sb);
				
				if (sb.length() == lastLength)
					sb.append(", ");
			}
		}
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		final Entity other = (Entity) obj;
		
		return id.equals(other.id);
	}
}