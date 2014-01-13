package de.algorythm.jdoe.cache;

import java.lang.ref.ReferenceQueue;

public class SoftCacheReferenceFactory<V> implements ICacheReferenceFactory<V> {

	@Override
	public ICacheReference<V> create(final String key, final V referent,
			final ReferenceQueue<? super V> queue) {
		return new SoftCacheReference<V>(key, referent, queue);
	}
}
