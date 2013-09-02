package de.algorythm.jdoe.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class CacheObjectReference<V> extends WeakReference<V> {

	private final String key;
	
	public CacheObjectReference(final String key, final V referent,
			final ReferenceQueue<? super V> queue) {
		super(referent, queue);
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
}