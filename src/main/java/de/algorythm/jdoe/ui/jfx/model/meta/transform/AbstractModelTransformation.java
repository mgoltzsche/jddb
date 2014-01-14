package de.algorythm.jdoe.ui.jfx.model.meta.transform;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;

public abstract class AbstractModelTransformation<S,T> {

	private final HashMap<S, T> transformationMap = new HashMap<>();
	private final LinkedList<Procedure0> lateTransformations = new LinkedList<>();
	
	public Collection<T> transform(final Collection<S> objects) {
		final LinkedList<T> result = new LinkedList<>();
		
		for (S object : objects) {
			final T transformed = transform(object);
			
			transformationMap.put(object, transformed);
			result.add(transformed);
		}
		
		for (Procedure0 lateTransformation : lateTransformations)
			lateTransformation.apply();
		
		transformationMap.clear();
		lateTransformations.clear();
		
		return result;
	}
	
	protected void transformLater(final Procedure0 transformation) {
		lateTransformations.add(transformation);
	}
	
	protected T getTransformed(S object) {
		return transformationMap.get(object);
	}
	
	protected abstract T transform(S object);
}
