package de.algorythm.jdoe.model.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class ModelChange<V extends IEntity<P,REF>, P extends IPropertyValue<?, REF>, REF extends IEntityReference> {

	private final Map<String,V> saved = new HashMap<>();
	private final Set<V> deleted = new HashSet<>();
	private boolean newOrDeleted;
	
	public Map<String,V> getSaved() {
		return saved;
	}
	
	public boolean isNewOrDeleted() {
		return newOrDeleted;
	}

	public void setNewOrDeleted(boolean newOrDeleted) {
		this.newOrDeleted = newOrDeleted;
	}

	public Set<V> getDeleted() {
		return deleted;
	}
}
