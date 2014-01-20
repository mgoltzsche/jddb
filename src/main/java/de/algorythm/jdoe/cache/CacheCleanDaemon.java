package de.algorythm.jdoe.cache;

import java.lang.ref.ReferenceQueue;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheCleanDaemon<V> extends Thread {

	static private final Logger log = LoggerFactory.getLogger(CacheCleanDaemon.class);
	
	private final Map<String, ICacheReference<V>> cacheMap;
	private final ReferenceQueue<? extends V> removalQueue;
	private final Object syncObject;
	
	public CacheCleanDaemon(final String name, final Object syncObject, final Map<String, ICacheReference<V>> cacheMap, final ReferenceQueue<? extends V> removalQueue) {
		super(name + "-clean-daemon");
		
		this.syncObject = syncObject;
		this.cacheMap = cacheMap;
		this.removalQueue = removalQueue;
		
		setDaemon(true);
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				@SuppressWarnings("unchecked")
				final ICacheReference<? extends V> removeRef = (ICacheReference<? extends V>) removalQueue.remove();
				final String key = removeRef.getKey();
				
				synchronized(syncObject) {
					final ICacheReference<? extends V> mappedRef = cacheMap.get(key);
					
					if (mappedRef == removeRef) {
						cacheMap.remove(key);
						log.debug("cleaned cache key " + key);
					}
					
					removeRef.clear();
				}
			}
		} catch(InterruptedException e) {
			log.debug(getName() + " interrupted / stopped");
		}
	}
	
	@Override
	public void start() {
		super.start();
		log.debug(getName() + " started");
	}
}