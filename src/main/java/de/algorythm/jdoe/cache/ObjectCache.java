package de.algorythm.jdoe.cache;

import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.xtext.xbase.lib.Functions.Function1;

public class ObjectCache<V> implements IObjectCache<V> {

	private final Map<String, ICacheReference<V>> cacheMap = new HashMap<>();
	private final ReferenceQueue<V> removalQueue = new ReferenceQueue<>();
	private final ICacheReferenceFactory<V> referenceFactory;
	
	public ObjectCache(final String name, final ICacheReferenceFactory<V> referenceFactory) {
		this.referenceFactory = referenceFactory;
		new CacheCleanDaemon<V>(name, this, removalQueue, cacheMap).start();
	}
	
	public void put(final String key, final V obj) {
		if (obj == null)
			throw new IllegalArgumentException("Cannot cache null");
		
		synchronized(this) {
			if (cacheMap.containsKey(key))
				throw new IllegalArgumentException("object with key " + key + " is already cached");
			
			cacheMap.put(key, referenceFactory.create(key, obj, removalQueue));
		}
	}
	
	public V get(final String key) {
		final ICacheReference<V> ref;
		
		synchronized(this) {
			ref = cacheMap.get(key);
		}
		
		if (ref == null)
			return null;
		else
			return ref.get();
	}
	
	public V get(final String key, final Function1<String, V> loader) {
		synchronized(this) {
			V value = get(key);
			
			if (value == null) {
				value = loader.apply(key);
				
				put(key, value);
			}
			
			return value;
		}
	}
	
	public int size() {
		synchronized(this) {
			return cacheMap.size();
		}
	}
}