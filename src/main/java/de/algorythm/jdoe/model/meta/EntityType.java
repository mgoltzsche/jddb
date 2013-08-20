package de.algorythm.jdoe.model.meta;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import de.algorythm.jdoe.model.dao.IPropertyValueFactory;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;

public class EntityType extends AbstractLabeledElement implements IPropertyType<IEntityReference>, Serializable {

	static private final long serialVersionUID = 2546803693147036351L;
	static public final EntityType ALL = new EntityType("All");
	
	private Collection<Property> properties = new LinkedList<>();

	public EntityType() {
		setLabel("New type");
	}
	
	public EntityType(final String label) {
		setLabel(label);
	}
	
	public Collection<Property> getProperties() {
		return properties;
	}
	
	public void setProperties(final Collection<Property> properties) {
		this.properties = properties;
	}
	
	@Override
	public boolean isUserDefined() {
		return true;
	}
	
	@Override
	public <P extends IPropertyValue<?>> P createPropertyValue(final Property property, final IPropertyValueFactory<P> factory) {
		return factory.createPropertyValue(property, this);
	}
	
	@Override
	public void doWithPropertyValue(IPropertyValue<IEntityReference> value,
			IPropertyValueVisitor visitor) {
		visitor.doWithAssociation(value);
	}
	
	public boolean valueChanged(final IEntityReference oldValue, final IEntityReference newValue) {
		return oldValue == null && newValue != null || oldValue != null && !oldValue.equals(newValue);
	}
	
	@Override
	public boolean isConform(final IPropertyType<?> type) {
		return type == null ? false : label.equals(type.getLabel());
	}

	@Override
	public String toString() {
		return label;
	}

	@Override
	public void valueToString(final IEntityReference value,
			final StringBuilder sb) {
		if (value != null)
			value.toString(sb);
	}
}