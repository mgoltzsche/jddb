package de.algorythm.jdoe.cache;

import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.xtext.xbase.lib.Functions.Function1;

public class ObjectCache<V> implements IObjectCache<V> {

	private final String name;
	private final Map<String, ICacheReference<V>> cacheMap = new HashMap<>();
	private final ICacheReferenceFactory<V> referenceFactory;
	private ReferenceQueue<V> removalQueue;
	private CacheCleanDaemon<V> cacheCleanDaemon;
	
	public ObjectCache(final String name, final ICacheReferenceFactory<V> referenceFactory) {
		this.name = name;
		this.referenceFactory = referenceFactory;
		createCacheCleanDaemon();
	}
	
	private void createCacheCleanDaemon() {
		removalQueue = new ReferenceQueue<>();
		cacheCleanDaemon = new CacheCleanDaemon<V>(name, this, removalQueue, cacheMap);
		cacheCleanDaemon.start();
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
	
	@Override
	public void clear() {
		synchronized(this) {
			cacheCleanDaemon.interrupt();
			
			try {
				cacheCleanDaemon.join();
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted while waiting for " + cacheCleanDaemon.getName(), e);
			}
			
			cacheMap.clear();
			createCacheCleanDaemon();
		}
	}
}