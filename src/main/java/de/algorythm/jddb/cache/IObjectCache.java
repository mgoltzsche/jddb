package de.algorythm.jddb.cache;

public interface IObjectCache<V> {

	void put(String key, V value);
	V get(String key);
	V get(String key, ICachePopulator<V> loader);
	int size();
	void clear();
}