package de.algorythm.jddb.cache;

import java.lang.ref.ReferenceQueue;

public class WeakCacheReferenceFactory<V> implements ICacheReferenceFactory<V> {

	@Override
	public ICacheReference<V> create(final String key, final V referent,
			final ReferenceQueue<? super V> queue) {
		return new WeakCacheReference<V>(key, referent, queue);
	}
}
