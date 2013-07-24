package de.algorythm.jdoe.model.dao.impl.ser;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.meta.Schema;

public class DatabaseContainer implements Serializable {
	
	static private final long serialVersionUID = 7004996175995444923L;
	
	private Schema schema = new Schema();
	private Set<IEntity> entities = new HashSet<>();
	private TreeMap<String, Set<IEntity>> index = new TreeMap<>();
	private TreeMap<IEntity, Collection<String>> entityIndex = new TreeMap<>();
	
	public Schema getSchema() {
		return schema;
	}
	
	public void setSchema(Schema schema) {
		this.schema = schema;
	}
	
	public Collection<IEntity> getEntities() {
		return entities;
	}
	
	public TreeMap<String, Set<IEntity>> getIndex() {
		return index;
	}
	
	public TreeMap<IEntity, Collection<String>> getObjectIndex() {
		return entityIndex;
	}
}