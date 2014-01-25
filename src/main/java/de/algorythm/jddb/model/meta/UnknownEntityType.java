package de.algorythm.jddb.model.meta;

import de.algorythm.jddb.bundle.Bundle;

public class UnknownEntityType extends MEntityType {

	static private final long serialVersionUID = -7360987287928027937L;
	static private MEntityType instance;
	
	static public final MEntityType getInstance() {
		if (instance == null)
			instance = new UnknownEntityType();
		
		return instance;
	}
	
	private UnknownEntityType() {
		setLabel(Bundle.getInstance().unknown);
	}
	
	@Override
	public boolean isConform(final IPropertyType<?> type) {
		return true;
	}
}
