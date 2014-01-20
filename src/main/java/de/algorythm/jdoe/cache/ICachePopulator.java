package de.algorythm.jdoe.cache;

public interface ICachePopulator<V> {

	V load(String key);
}
