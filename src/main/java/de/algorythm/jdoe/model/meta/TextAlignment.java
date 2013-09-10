package de.algorythm.jdoe.model.meta;

public enum TextAlignment {
	LEFT("text-alignment-left"),
	CENTER("text-alignment-center"),
	RIGHT("text-alignment-right");
	
	private final String alignment;
	
	TextAlignment(final String alignment) {
		this.alignment = alignment;
	}
	
	public String toString() {
		return alignment;
	}
}
