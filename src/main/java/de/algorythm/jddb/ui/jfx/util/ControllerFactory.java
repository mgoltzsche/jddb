package de.algorythm.jddb.ui.jfx.util;

import javafx.util.Callback;

import com.google.inject.Injector;

public class ControllerFactory implements Callback<Class<?>, Object>{

	private final Injector injector;
	
	public ControllerFactory(final Injector injector) {
		this.injector = injector;
	}
	
	@Override
	public Object call(final Class<?> ctrlType) {
		return injector.getInstance(ctrlType);
	}

}
