package de.algorythm.jdoe.model.meta;

import java.io.Serializable;

public abstract class AbstractLabeledElement implements Serializable, ILabeledElement {

	private static final long serialVersionUID = 4039667635852880444L;
	
	private String name;
	protected String label;
	
	public String getName() {
		return name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public void setLabel(final String label) {
		this.label = label.trim();
		name = this.label.replaceAll("[^A-Za-z0-9_]", "");
	}
	
	@Override
	public String toString() {
		return label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractLabeledElement other = (AbstractLabeledElement) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
