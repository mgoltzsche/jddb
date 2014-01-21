package de.algorythm.jddb.cache;

import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

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
		cacheCleanDaemon = new CacheCleanDaemon<V>(name, this, cacheMap, removalQueue);
		cacheCleanDaemon.start();
	}
	
	@Override
	public synchronized void put(final String key, final V value) {
		if (key == null)
			throw new IllegalArgumentException("Cache key cannot be null");
		
		if (value == null)
			throw new IllegalArgumentException("Cache value cannot be null");
		
		if (get(key) != null)
			throw new IllegalArgumentException("object with key " + key + " is already cached");
		
		cacheMap.put(key, referenceFactory.create(key, value, removalQueue));
	}
	
	@Override
	public synchronized V get(final String key) {
		final ICacheReference<V> ref = cacheMap.get(key);
		
		if (ref == null)
			return null;
		else
			return ref.get();
	}
	
	@Override
	public synchronized V get(final String key, final ICachePopulator<V> loader) {
		V result = get(key);
		
		if (result == null) {
			result = loader.load(key);
			
			put(key, result);
		}
		
		return result;
	}
	
	@Override
	public synchronized int size() {
		return cacheMap.size();
	}
	
	@Override
	public synchronized void clear() {
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