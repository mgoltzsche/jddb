package de.algorythm.jddb.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class WeakCacheReference<V> extends WeakReference<V> implements
		ICacheReference<V> {

	private final String key;

	public WeakCacheReference(final String key, final V referent,
			final ReferenceQueue<? super V> queue) {
		super(referent, queue);
		this.key = key;
	}

	@Override
	public String getKey() {
		return key;
	}
}