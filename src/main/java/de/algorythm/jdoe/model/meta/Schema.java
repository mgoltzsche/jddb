package de.algorythm.jdoe.model.meta;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Schema implements Serializable {

	private static final long serialVersionUID = 6465812673182870098L;
	
	private Collection<MEntityType> types = new LinkedList<>();
	private transient Map<String, MEntityType> typeIndex;

	public Collection<MEntityType> getTypes() {
		return types;
	}

	public void setTypes(final Collection<MEntityType> types) {
		this.types = types;
	}
	
	public MEntityType getTypeByName(final String name) {
		if (typeIndex == null) {
			typeIndex = new HashMap<String, MEntityType>();
			
			for (MEntityType type : types)
				typeIndex.put(type.getName(), type);
		}
		
		return typeIndex.get(name);
	}
}
