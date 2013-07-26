package de.algorythm.jdoe.ui.jfx.util;

import java.io.IOException;

import javafx.fxml.FXMLLoader;

import javax.inject.Inject;

import com.google.inject.Injector;

import de.algorythm.jdoe.controller.IController;

public class GuiceFxmlLoader {

	@Inject private Injector injector;
	
	@SuppressWarnings("unchecked")
	public <N, C extends IController> FxmlLoaderResult<N, C> load(final String fxmlFileName) throws IOException {
		final FXMLLoader loader = new FXMLLoader();
		final N node = (N) loader.load(getClass().getResourceAsStream(fxmlFileName));
		final C controller;
		
		try {
			controller = (C) loader.getController();
		} catch(ClassCastException e) {
			throw new RuntimeException("controller must implement IController", e);
		}
		
		if (controller != null) {
			injector.injectMembers(controller);
			controller.init();
		}
		
		return new FxmlLoaderResult<N, C>(node, controller);
	}
}