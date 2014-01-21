package de.algorythm.jddb.model.entity.impl;

import java.util.ArrayList;
import java.util.UUID;

import de.algorythm.jddb.model.entity.IEntity;
import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.meta.MEntityType;

public abstract class AbstractEntity<P extends IPropertyValue<?,REF>, REF extends IEntityReference> implements IEntity<P, REF> {

	static private final long serialVersionUID = 8803662114651751761L;
	
	private final String id;
	private final MEntityType type;
	private ArrayList<P> values;
	
	public AbstractEntity(final MEntityType type) {
		this(UUID.randomUUID().toString(), type);
	}
	
	public AbstractEntity(final String id, final MEntityType type) {
		this.id = id;
		this.type = type;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public MEntityType getType() {
		return type;
	}
	
	@Override
	public ArrayList<P> getValues() {
		return values;
	}
	
	@Override
	public void setValues(final ArrayList<P> values) {
		this.values = values;
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
