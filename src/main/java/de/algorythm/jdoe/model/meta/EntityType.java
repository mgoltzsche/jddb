package de.algorythm.jdoe.model.meta;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueFactory;

public class EntityType extends AbstractLabeledElement implements IPropertyType<IEntityReference>, Serializable {

	static private final long serialVersionUID = 2546803693147036351L;
	
	private Collection<Property> properties = new LinkedList<>();
	private boolean embedded;

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
	
	public boolean isEmbedded() {
		return embedded;
	}
	
	public void setEmbedded(final boolean embedded) {
		this.embedded = embedded;
	}
	
	@Override
	public boolean isUserDefined() {
		return Boolean.TRUE;
	}
	
	@Override
	public <P extends IPropertyValue<?,REF>, REF extends IEntityReference> P createPropertyValue(final Property property, final IPropertyValueFactory<P,REF> factory) {
		return factory.createAssociationValue(property);
	}
	
	@Override
	public boolean isConform(final IPropertyType<?> type) {
		return type == null ? false : label.equals(type.getLabel());
	}
	
	@Override
	public int compare(final IEntityReference a, final IEntityReference b) {
		return a.toString().compareToIgnoreCase(b.toString());
	}
	
	@Override
	public String toString() {
		return label;
	}
}