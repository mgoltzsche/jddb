package de.algorythm.jdoe.cache;

public interface ICacheReference<V> {
	public V get();
	public void clear();
	public String getKey();
}
