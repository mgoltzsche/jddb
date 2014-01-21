package de.algorythm.jddb.cache;

public interface ICacheReference<V> {
	public V get();
	public void clear();
	public String getKey();
}
