package de.algorythm.jddb.test;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import de.algorythm.jddb.cache.ObjectCache;
import de.algorythm.jddb.cache.WeakCacheReferenceFactory;

public class CacheTest {

	private final ObjectCache<Object> cache = new ObjectCache<>("object-cache", new WeakCacheReferenceFactory<>());
	
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
	
	@Test
	public void WeaklyReferencedCachedObjectShouldBeRemoved() {
		MyObject obj = new MyObject(null);
		final WeakReference<MyObject> weakRef = new WeakReference<MyObject>(obj);
		
		cache.put(obj.getId(), obj);
		
		Assert.assertEquals(1, cache.size());
		
		obj = null;
		
		System.gc();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new AssertionError(e);
		}
		
		Assert.assertEquals("Cache should be cleared", 0, cache.size());
		Assert.assertNull("Weak reference to expired cached object should be null", weakRef.get());
	}
	
	@Test
	public void CachedObjectWithCyclicStrongReferencesShouldBeRemoved() {
		MyObject innerObj = new MyObject(null);
		MyObject obj = new MyObject(innerObj);
		MyObject cyclicObj = new MyObject(obj);
		innerObj.setRef(cyclicObj);
		
		cache.put(innerObj.getId(), innerObj);
		cache.put(obj.getId(), obj);
		cache.put(cyclicObj.getId(), cyclicObj);
		
		Assert.assertEquals(3, cache.size());
		
		obj = innerObj = cyclicObj = null;
		
		System.gc();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new AssertionError(e);
		}
		
		Assert.assertEquals("Cache should be cleared", 0, cache.size());
	}
	
	/*@Test
	public void CachedEntitiesShouldBeRemoved() {
		final SimpleStringProperty boundProperty = new SimpleStringProperty();
		final EntityType type = new EntityType("Person");
		FXEntity entity = new FXEntity(type);
		final Property property = new Property();
		property.setType(AbstractAttributeType.ATTRIBUTE_TYPES.iterator().next());
		@SuppressWarnings("unchecked")
		final IFXPropertyValue<String> propertyValue = (IFXPropertyValue<String>) property.createPropertyValue(new FXModelFactory());
		final ArrayList<IFXPropertyValue<?>> propertyValues = new ArrayList<IFXPropertyValue<?>>(1);
		propertyValues.add(propertyValue);
		entity.setValues(propertyValues);
		
		boundProperty.bind(entity.labelProperty());
		
		propertyValue.setValue("testvalue"); // IllegalStateException: Toolkit not initialized (Platform.runLater())
		
		cache.put(entity.getId(), entity);
		
		Assert.assertEquals(1, cache.size());
		
		entity = null;
		
		System.gc();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new AssertionError(e);
		}
		
		Assert.assertEquals("Cache should be cleared", 0, cache.size());
	}*/
}
