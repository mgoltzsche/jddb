package de.algorythm.jddb.model.entity.impl.propertyValue;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.meta.IPropertyType;
import de.algorythm.jddb.model.meta.MProperty;

public abstract class AbstractPropertyValue<V> implements IPropertyValue<V,IEntityReference> {

	static private final long serialVersionUID = 3601500282325296848L;
	static protected final String EMPTY = "";
	
	private V value;
	private MProperty property;
	protected IPropertyType<?> type;

	public AbstractPropertyValue(final MProperty property) {
		this.property = property;
		this.type = property.getType();
	}
	
	@Override
	public MProperty getProperty() {
		return property;
	}
	
	@Override
	public V getValue() {
		return value;
	}
	
	@Override
	public void setValue(final V value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}
}
