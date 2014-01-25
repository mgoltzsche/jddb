package de.algorythm.jddb.model.meta;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.entity.IPropertyValueFactory;

public class MEntityType extends AbstractLabeledElement implements IPropertyType<IEntityReference>, Serializable {

	static private final long serialVersionUID = 2546803693147036351L;
	
	private Collection<MProperty> properties = new LinkedList<>();
	private boolean embedded;

	public MEntityType() {
		setLabel("New type");
	}
	
	public MEntityType(final String label) {
		setLabel(label);
	}
	
	public Collection<MProperty> getProperties() {
		return properties;
	}
	
	public void setProperties(final Collection<MProperty> properties) {
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
	public <P extends IPropertyValue<?,? extends IEntityReference>> P createPropertyValue(final MProperty property, final IPropertyValueFactory<P> factory) {
		return factory.createAssociationValue(property);
	}
	
	@Override
	public boolean isConform(final IPropertyType<?> type) {
		return label.equals(type.getLabel());
	}
	
	@Override
	public TextAlignment getTextAlignment() {
		return TextAlignment.LEFT;
	}
	
	@Override
	public String toString() {
		return label;
	}
}