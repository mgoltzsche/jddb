package de.algorythm.jdoe.ui.jfx.util;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.inject.Inject;

import com.google.inject.Injector;

import de.algorythm.jdoe.bundle.Bundle;
import de.algorythm.jdoe.controller.IController;

public class GuiceFxmlLoader {

	@Inject private Injector injector;
	@Inject private Bundle bundle;
	
	@SuppressWarnings("unchecked")
	public <N, C extends IController> FxmlLoaderResult<N, C> load(final String fxmlFileName) throws IOException {
		final FXMLLoader loader = new FXMLLoader();
		loader.setResources(bundle.bundle);
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
	
	public void showWindow(final String fxmlFileName, final String title, final int width, final int height) throws IOException {
		final FxmlLoaderResult<Parent, IController> loaderResult = load(fxmlFileName);
		final Parent rootNode = loaderResult.getNode();
		final Scene scene = new Scene(rootNode, width, height);
		final Stage stage = new Stage();
		
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
	}
}