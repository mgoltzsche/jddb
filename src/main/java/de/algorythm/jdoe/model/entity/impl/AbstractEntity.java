package de.algorythm.jdoe.model.entity.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.EntityType;

public class AbstractEntity<REF extends IEntityReference> implements IEntity<REF> {

	static private final long serialVersionUID = 8803662114651751761L;
	
	private final String id;
	private final EntityType type;
	private final ArrayList<? extends IPropertyValue<?,REF>> values;
	private final Collection<REF> referencingEntities;
	private transient boolean transientInstance;
	
	public AbstractEntity(final EntityType type, final ArrayList<? extends IPropertyValue<?,REF>> values) {
		this(UUID.randomUUID().toString(), type, values, new LinkedList<REF>());
		transientInstance = true;
	}
	
	public AbstractEntity(final String id, final EntityType type, final ArrayList<? extends IPropertyValue<?,REF>> values, final Collection<REF> referencingEntities) {
		this.id = id;
		this.type = type;
		this.values = values;
		this.referencingEntities = referencingEntities;
	}
	
	public AbstractEntity(final String id, final EntityType type, final ArrayList<? extends IPropertyValue<?,REF>> values) {
		this.id = id;
		this.type = type;
		referencingEntities = null;
		this.values = values;
	}
	
	/*public AbstractEntity(final Entity<REF> entity) {
		id = entity.id;
		type = entity.type;
		values = new ArrayList<>(entity.values);
		referencingEntities = entity.referencingEntities;
		transientInstance = entity.transientInstance;
	}*/
	
	@Override
	public String getId() {
		return id;
	}
	
	public void setTransientInstance(final boolean transientInstance) {
		this.transientInstance = transientInstance;
	}
	
	@Override
	public boolean isChanged() {
		for (IPropertyValue<?,REF> value : values)
			if (value.isChanged())
				return true;
		
		return false;
	}
	
	@Override
	public EntityType getType() {
		return type;
	}
	
	@Override
	public Collection<? extends IPropertyValue<?,REF>> getValues() {
		return values;
	}
	
	@Override
	public Iterable<REF> getReferencingEntities() {
		return referencingEntities;
	}
	
	@Override
	public boolean isTransientInstance() {
		return transientInstance;
	}
	
	@Override
	public IEntity<?> getTransientInstance() {
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
		for (IPropertyValue<?,REF> value : getValues()) {
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
		
		final AbstractEntity<?> other = (AbstractEntity<?>) obj;
		
		return id.equals(other.id);
	}
}
