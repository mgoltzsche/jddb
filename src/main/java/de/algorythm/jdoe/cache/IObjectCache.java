package de.algorythm.jdoe.cache;

public interface IObjectCache<V> {

	V get(String key);
	void put(String key, V value);
	int size();
}