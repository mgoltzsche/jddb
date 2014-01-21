package de.algorythm.jddb.model.meta;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.entity.IPropertyValueFactory;
import de.algorythm.jddb.model.meta.propertyTypes.TString;

public class MProperty extends AbstractLabeledElement {
	
	static private final long serialVersionUID = -1803072858495973198L;
	
	static private final IPropertyType<?> DEFAULT_TYPE = new TString();
	
	private IPropertyType<?> type = DEFAULT_TYPE;
	private boolean containment, searchable = true;
	
	public MProperty() {
		setLabel("New property");
	}
	
	public IPropertyType<?> getType() {
		return type;
	}
	
	public void setType(final IPropertyType<?> type) {
		this.type = type;
	}
	
	public boolean isContainment() {
		return containment;
	}
	
	public void setContainment(boolean containment) {
		this.containment = containment;
	}
	
	public boolean isSearchable() {
		return searchable;
	}
	
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}
	
	public <P extends IPropertyValue<?, ? extends IEntityReference>> P createPropertyValue(final IPropertyValueFactory<P> factory) {
		return type.createPropertyValue(this, factory);
	}
	
	@Override
	public String toString() {
		return label;
	}
}
