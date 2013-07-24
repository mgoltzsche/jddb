package de.algorythm.jdoe.model.meta;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.attributeTypes.TString;
import de.algorythm.jdoe.model.meta.visitor.IPropertyValueVisitor;
import de.algorythm.jdoe.model.meta.visitor.PropertyVisitorContext;

public class Property extends AbstractLabeledElement {
	
	static private final long serialVersionUID = -1803072858495973198L;
	
	static private final IPropertyType DEFAULT_TYPE = new TString();
	
	private IPropertyType type = DEFAULT_TYPE;
	private int index;
	private boolean containment, collection, searchable = true;
	private transient PropertyVisitorContext visitorContext;
	
	public Property() {
		setLabel("New property");
	}
	
	public IPropertyType getType() {
		return type;
	}
	
	public void setType(final IPropertyType type) {
		this.type = type;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public boolean isContainment() {
		return containment;
	}
	
	public void setContainment(boolean containment) {
		this.containment = containment;
	}
	
	public boolean isCollection() {
		return collection;
	}
	
	public void setCollection(boolean collection) {
		this.collection = collection;
	}
	
	public boolean isSearchable() {
		return searchable;
	}
	
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}
	
	public <T> void doWithPropertyValue(final IPropertyValue propertyValue,
			final IPropertyValueVisitor visitor) {
		if (visitorContext == null)
			visitorContext = PropertyVisitorContext.createFor(propertyValue.getProperty());
		
		visitorContext.doWithPropertyValue(propertyValue, visitor);
	}
	
	public String toString(final IPropertyValue propertyValue) {
		return visitorContext.toString(propertyValue);
	}
	
	@Override
	public String toString() {
		return label;
	}
}
