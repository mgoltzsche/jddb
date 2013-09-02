package de.algorythm.jdoe.cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Map;

public class CacheCleanDeamon<V> extends Thread {

	private final ReferenceQueue<V> removalQueue;
	private final Map<String, Reference<V>> cacheMap;
	
	public CacheCleanDeamon(final ReferenceQueue<V> removalQueue, final Map<String, Reference<V>> cacheMap) {
		super();
		
		this.removalQueue = removalQueue;
		this.cacheMap = cacheMap;
		
		setDaemon(true);
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				final CacheObjectReference<? extends V> removeObj = (CacheObjectReference<? extends V>) removalQueue.remove();
				
				synchronized(cacheMap) {
					final String key = removeObj.getKey();
					cacheMap.remove(key);
					removeObj.clear();
				}
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}