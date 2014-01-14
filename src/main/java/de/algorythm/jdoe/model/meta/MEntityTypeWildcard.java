package de.algorythm.jdoe.model.meta;

import de.algorythm.jdoe.bundle.Bundle;

public class MEntityTypeWildcard extends MEntityType {

	static private final long serialVersionUID = 659038941492670930L;
	static public final MEntityType INSTANCE = new MEntityTypeWildcard();
	
	private MEntityTypeWildcard() {
		setLabel(Bundle.getInstance().all);
	}
	
	@Override
	public boolean isConform(final IPropertyType<?> type) {
		return true;
	}
}
