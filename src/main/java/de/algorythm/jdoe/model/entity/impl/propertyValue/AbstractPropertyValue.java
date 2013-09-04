package de.algorythm.jdoe.model.entity.impl.propertyValue;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.IPropertyType;
import de.algorythm.jdoe.model.meta.Property;

public abstract class AbstractPropertyValue<V,REF extends IEntityReference> implements IPropertyValue<V,REF> {

	static private final long serialVersionUID = 3601500282325296848L;
	static protected final String EMPTY = "";
	
	private V value;
	private Property property;
	protected IPropertyType<?> type;
	protected transient boolean pristine = true;

	public AbstractPropertyValue(final Property property) {
		this.property = property;
		this.type = property.getType();
	}
	
	@Override
	public Property getProperty() {
		return property;
	}
	
	@Override
	public boolean isPristine() {
		return pristine;
	}
	
	@Override
	public void setPristine(final boolean pristine) {
		this.pristine = pristine;
	}
	
	@Override
	public V getValue() {
		return value;
	}
	
	@Override
	public void setValue(final V value) {
		if (valueChanged(this.value, value)) {
			this.value = value;
			pristine = false;
		}
	}
	
	protected boolean valueChanged(V oldValue, V newValue) {
		return oldValue == null && newValue != null || oldValue != null && !oldValue.equals(newValue);
	}
	
	/*@Override
	public void doWithValue(final IPropertyValueVisitor<ENTITYREF> visitor) {
		type.doWithPropertyValue(this, visitor);
	}
	
	@Override
	public void toString(final StringBuilder sb) {
		type.valueToString(value, sb);
	}*/
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}
}
