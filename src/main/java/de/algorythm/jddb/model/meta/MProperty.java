package de.algorythm.jddb.model.meta;

import java.util.Map;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.entity.IPropertyValueFactory;
import de.algorythm.jddb.model.meta.propertyTypes.TString;

public class MProperty extends AbstractLabeledElement {
	
	static private final long serialVersionUID = -1803072858495973198L;
	
	private String typeName = TString.getInstance().getName();
	private transient IPropertyType<?> type = TString.getInstance();
	private boolean containment, searchable = true;
	
	public MProperty() {
		setLabel("New property");
	}
	
	public void loadTypeForName(final Map<String, IPropertyType<?>> typeMap) {
		if (typeName == null)
			throw new IllegalStateException("typeName is null");
		
		type = typeMap.get(typeName);
		
		if (type == null)
			throw new IllegalStateException("Unknown type name " + typeName);
	}
	
	public IPropertyType<?> getType() {
		return type;
	}
	
	public void setType(final IPropertyType<?> type) {
		this.type = type;
		typeName = type.getName();
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
