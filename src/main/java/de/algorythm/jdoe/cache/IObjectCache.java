package de.algorythm.jdoe.cache;

public interface IObjectCache<V> {

	void put(final String key, final V obj);
	V get(final String key);
	int size();
}