package de.algorythm.jddb.cache;

import java.lang.ref.ReferenceQueue;

public interface ICacheReferenceFactory<V> {
	ICacheReference<V> create(String key, V referent, ReferenceQueue<? super V> queue);
}
