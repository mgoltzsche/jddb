package de.algorythm.jdoe.model.meta;

import java.io.Serializable;

public abstract class AbstractLabeledElement implements Serializable {

	private static final long serialVersionUID = 4039667635852880444L;
	
	private String name;
	protected String label;
	
	public String getName() {
		return name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(final String label) {
		this.label = label.trim();
		name = this.label.replaceAll("[^A-Za-z0-9_]", "");
	}
	
	@Override
	public String toString() {
		return label;
	}
}
