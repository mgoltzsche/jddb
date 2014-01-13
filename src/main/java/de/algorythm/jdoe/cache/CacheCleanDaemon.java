package de.algorythm.jdoe.cache;

import java.lang.ref.ReferenceQueue;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheCleanDaemon<V> extends Thread {

	static private final Logger LOG = LoggerFactory.getLogger(CacheCleanDaemon.class);	
	
	private final Object syncMonitor;
	private final ReferenceQueue<V> removalQueue;
	private final Map<String, ICacheReference<V>> cacheMap;
	
	public CacheCleanDaemon(final String name, final Object syncMonitor, final ReferenceQueue<V> removalQueue, final Map<String, ICacheReference<V>> cacheMap) {
		super(name + "-clean-daemon");
		
		this.syncMonitor = syncMonitor;
		this.removalQueue = removalQueue;
		this.cacheMap = cacheMap;
		
		setDaemon(true);
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				@SuppressWarnings("unchecked")
				final ICacheReference<? extends V> removeObj = (ICacheReference<? extends V>) removalQueue.remove();
				
				synchronized(syncMonitor) {
					final String key = removeObj.getKey();
					cacheMap.remove(key);
					removeObj.clear();
					LOG.debug("cleaned cache key: " + key);
				}
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}