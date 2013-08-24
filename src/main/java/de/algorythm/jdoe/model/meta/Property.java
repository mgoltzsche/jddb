package de.algorythm.jdoe.model.meta;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueFactory;
import de.algorythm.jdoe.model.meta.propertyTypes.TString;

public class Property extends AbstractLabeledElement {
	
	static private final long serialVersionUID = -1803072858495973198L;
	
	static private final IPropertyType<?> DEFAULT_TYPE = new TString();
	
	private IPropertyType<?> type = DEFAULT_TYPE;
	private boolean containment, searchable = true;
	
	public Property() {
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
	
	public <REF extends IEntityReference, P extends IPropertyValue<?,REF>> P createPropertyValue(final IPropertyValueFactory<REF,P> factory) {
		return type.createPropertyValue(this, factory);
	}
	
	@Override
	public String toString() {
		return label;
	}
}
