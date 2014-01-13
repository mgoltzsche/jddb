package de.algorythm.jdoe.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

public class SoftCacheReference<V> extends SoftReference<V> implements
		ICacheReference<V> {

	private final String key;

	public SoftCacheReference(final String key, final V referent,
			final ReferenceQueue<? super V> queue) {
		super(referent, queue);
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}