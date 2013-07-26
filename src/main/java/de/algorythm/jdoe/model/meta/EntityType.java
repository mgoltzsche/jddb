package de.algorythm.jdoe.model.meta;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

public class EntityType extends AbstractLabeledElement implements IPropertyType, Serializable {

	static private final long serialVersionUID = 2546803693147036351L;
	static public final EntityType DEFAULT = new EntityType();
	
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
	public String toString() {
		return label;
	}
}