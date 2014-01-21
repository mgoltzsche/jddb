package de.algorythm.jddb.ui.jfx.model;

public class ValueContainer<V> {

	private V value;

	public V getValue() {
		return value;
	}

	public void setValue(final V value) {
		this.value = value;
	}
}
