package de.algorythm.jdoe.cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

public class ObjectCache<V> {

	private final Map<String, Reference<V>> cacheMap = new HashMap<>();
	private final ReferenceQueue<V> removalQueue = new ReferenceQueue<>();
	
	public ObjectCache() {
		new CacheCleanDeamon<V>(removalQueue, cacheMap).start();
	}
	
	public void put(final String key, final V obj) {
		if (obj == null)
			throw new IllegalArgumentException("Cannot cache null");
		
		synchronized(cacheMap) {
			if (cacheMap.containsKey(key))
				throw new IllegalArgumentException("object with key " + key + " is already cached");
			
			cacheMap.put(key, new CacheObjectReference<V>(key, obj, removalQueue));
		}
	}
	
	public int size() {
		synchronized(cacheMap) {
			return cacheMap.size();
		}
	}
	
	public V get(final String key) {
		final Reference<V> ref;
		
		synchronized(cacheMap) {
			ref = cacheMap.get(key);
		}
		
		if (ref == null)
			return null;
		else
			return ref.get();
	}
}