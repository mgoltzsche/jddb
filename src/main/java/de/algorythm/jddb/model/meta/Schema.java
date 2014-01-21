package de.algorythm.jddb.model.meta;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.algorythm.jddb.model.dao.ISchema;

public class Schema implements ISchema {

	private static final long serialVersionUID = 6465812673182870098L;
	
	private Collection<MEntityType> types = new LinkedList<>();
	private transient Map<String, MEntityType> typeIndex;
	
	@Override
	public Collection<MEntityType> getTypes() {
		return types;
	}

	public void setTypes(final Collection<MEntityType> types) {
		this.types = types;
		typeIndex = null;
	}
	
	@Override
	public MEntityType getTypeByName(final String name) {
		return getTypeIndex().get(name);
	}
	
	@Override
	public boolean isKnownType(final String name) {
		return getTypeIndex().containsKey(name);
	}
	
	private Map<String, MEntityType> getTypeIndex() {
		if (typeIndex == null) {
			typeIndex = new HashMap<String, MEntityType>();
			
			for (MEntityType type : types)
				typeIndex.put(type.getName(), type);
		}
		
		return typeIndex;
	}
}
