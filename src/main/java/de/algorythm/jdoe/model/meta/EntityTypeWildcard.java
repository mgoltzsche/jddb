package de.algorythm.jdoe.model.meta;

import de.algorythm.jdoe.bundle.Bundle;

public class EntityTypeWildcard extends EntityType {

	static private final long serialVersionUID = 659038941492670930L;
	static public final EntityType INSTANCE = new EntityTypeWildcard();
	
	private EntityTypeWildcard() {
		setLabel(Bundle.getInstance().all);
	}
	
	@Override
	public boolean isConform(final IPropertyType<?> type) {
		return true;
	}
}
