package de.algorythm.jddb.model.entity.impl.propertyValue;

import de.algorythm.jddb.model.meta.MProperty;
import de.algorythm.jddb.model.meta.propertyTypes.AbstractAttributeType;

public abstract class AbstractAttributeValue<V extends Comparable<V>> extends AbstractPropertyValue<V> {

	static private final long serialVersionUID = 2179613684367210970L;

	private final AbstractAttributeType<V> type;
	
	public AbstractAttributeValue(final MProperty property, final AbstractAttributeType<V> type) {
		super(property);
		this.type = type;
	}

	@Override
	public void toString(final StringBuilder sb) {
		type.valueToString(getValue(), sb);
	}
}
