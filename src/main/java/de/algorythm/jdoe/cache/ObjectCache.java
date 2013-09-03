package de.algorythm.jdoe.cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

public class ObjectCache<V> implements IObjectCache<V> {

	private final Map<String, Reference<V>> cacheMap = new HashMap<>();
	private final ReferenceQueue<V> removalQueue = new ReferenceQueue<>();
	
	public ObjectCache() {
		new CacheCleanDaemon<V>(this, removalQueue, cacheMap).start();
	}
	
	public void put(final String key, final V obj) {
		if (obj == null)
			throw new IllegalArgumentException("Cannot cache null");
		
		synchronized(this) {
			if (cacheMap.containsKey(key))
				throw new IllegalArgumentException("object with key " + key + " is already cached");
			
			cacheMap.put(key, new CacheObjectReference<V>(key, obj, removalQueue));
		}
	}
	
	public V get(final String key) {
		final Reference<V> ref;
		
		synchronized(this) {
			ref = cacheMap.get(key);
		}
		
		if (ref == null)
			return null;
		else
			return ref.get();
	}
	
	public int size() {
		synchronized(this) {
			return cacheMap.size();
		}
	}
}