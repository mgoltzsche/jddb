package de.algorythm.jdoe.test;

import java.util.UUID;

public class MyObject {

	private final String id = UUID.randomUUID().toString();
	private MyObject ref;
	
	public MyObject(final MyObject ref) {
		this.ref = ref;
	}
	
	public String getId() {
		return id;
	}
	
	public MyObject getRef() {
		return ref;
	}
	
	public void setRef(final MyObject ref) {
		this.ref = ref;
	}
	
	@Override
	public String toString() {
		return "myObject";
	}
}
