package de.algorythm.jdoe.test;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import de.algorythm.jdoe.cache.ObjectCache;

public class CacheTest {

	private final ObjectCache<Object> cache = new ObjectCache<>();
	
	@Test
	public void GetShouldReturnCachedObject() {
		final String key = "a";
		final String obj = new String("asdf");
		
		cache.put(key, obj);
		
		Assert.assertEquals(obj, cache.get(key));
	}
	
	@Test
	public void CachedObjectWithSelfReferenceShouldBeGarbageCollected() {
		final int size = 20;
		LinkedList<MyObject> refs = new LinkedList<>();
		
		for (int i = 0; i < size; i++) {
			MyObject obj = new MyObject(null);
			obj.setRef(obj);
			refs.add(obj);
			cache.put(obj.getId(), obj);
		}
		
		Assert.assertEquals(size, cache.size());
		
		refs = null;
		
		System.gc();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new AssertionError(e);
		}
		
		Assert.assertEquals("Cache should be cleared", 0, cache.size());
	}
	
	@Test
	public void CachedObjectWithStrongReferenceToAnotherShouldAvoidRemovalOfTheOther() {
		MyObject innerObj = new MyObject(null);
		MyObject obj = new MyObject(innerObj);
		
		cache.put(innerObj.getId(), innerObj);
		cache.put(obj.getId(), obj);
		
		Assert.assertEquals(2, cache.size());
		
		innerObj = null;
		
		System.gc();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new AssertionError(e);
		}
		
		Assert.assertEquals("InnerObj should still exist", 2, cache.size());
	}
}
