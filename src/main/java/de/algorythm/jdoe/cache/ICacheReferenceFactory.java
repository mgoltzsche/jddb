package de.algorythm.jdoe.cache;

import java.lang.ref.ReferenceQueue;

public interface ICacheReferenceFactory<V> {
	ICacheReference<V> create(String key, V referent, ReferenceQueue<? super V> queue);
}
