package de.algorythm.jddb.cache;

public interface ICachePopulator<V> {

	V load(String key);
}
