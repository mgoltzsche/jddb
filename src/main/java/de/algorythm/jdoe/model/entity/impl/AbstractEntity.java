package de.algorythm.jdoe.model.entity.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueChangeHandler;
import de.algorythm.jdoe.model.meta.EntityType;

public class AbstractEntity<P extends IPropertyValue<?, REF>, REF extends IEntityReference> implements IEntity<P, REF>, IPropertyValueChangeHandler {

	static private final long serialVersionUID = 8803662114651751761L;
	
	private final String id;
	private final EntityType type;
	protected final ArrayList<P> values;
	private final Collection<REF> referencingEntities;
	protected transient boolean changed;
	
	public AbstractEntity(final EntityType type, final ArrayList<P> values) {
		this(UUID.randomUUID().toString(), type, values, new LinkedList<REF>());
	}
	
	public AbstractEntity(final String id, final EntityType type, final ArrayList<P> values, final Collection<REF> referencingEntities) {
		this.id = id;
		this.type = type;
		this.values = values;
		this.referencingEntities = referencingEntities;
		registerAsPropertyValueChangeHandler();
	}
	
	public AbstractEntity(final String id, final EntityType type, final ArrayList<P> values) {
		this.id = id;
		this.type = type;
		referencingEntities = null;
		this.values = values;
		registerAsPropertyValueChangeHandler();
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public boolean isChanged() {
		return changed;
	}
	
	@Override
	public void pristine() {
		changed = false;
		
		for (IPropertyValue<?,REF> value : values)
			value.pristine();
	}
	
	@Override
	public boolean changed() {
		return changed = true;
	}
	
	@Override
	public EntityType getType() {
		return type;
	}
	
	@Override
	public Collection<P> getValues() {
		return values;
	}
	
	@Override
	public Iterable<REF> getReferencingEntities() {
		return referencingEntities;
	}
	
	private void registerAsPropertyValueChangeHandler() {
		for (P value : values)
			value.setChangeHandler(this);
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
		
		final AbstractEntity<?,?> other = (AbstractEntity<?,?>) obj;
		
		return id.equals(other.id);
	}
}
