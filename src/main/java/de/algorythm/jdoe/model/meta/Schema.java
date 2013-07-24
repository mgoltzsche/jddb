package de.algorythm.jdoe.model.meta;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Schema implements Serializable {

	private static final long serialVersionUID = 6465812673182870098L;
	
	private Collection<EntityType> types = new LinkedList<>();
	private transient Map<String, EntityType> typeIndex;

	public Collection<EntityType> getTypes() {
		return types;
	}

	public void setTypes(final Collection<EntityType> types) {
		this.types = types;
	}
	
	public EntityType getTypeByName(final String name) {
		if (typeIndex == null) {
			typeIndex = new HashMap<String, EntityType>();
			
			for (EntityType type : types)
				typeIndex.put(type.getName(), type);
		}
		
		return typeIndex.get(name);
	}
}
