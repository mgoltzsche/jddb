package de.algorythm.jddb.ui.jfx.loader.fxml;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;

import com.google.inject.Injector;

import de.algorythm.jddb.bundle.Bundle;
import de.algorythm.jddb.ui.jfx.util.ControllerFactory;

public class GuiceFxmlLoader {

	@Inject private Injector injector;
	@Inject private Bundle bundle;
	
	@SuppressWarnings("unchecked")
	public <N, C> FxmlLoaderResult<N, C> load(final String fxmlFileName) throws IOException {
		final URL fxmlUrl = getClass().getResource(fxmlFileName);
		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation(fxmlUrl);
		loader.setResources(bundle.bundle);
		loader.setControllerFactory(new ControllerFactory(injector));
		final N node = (N) loader.load(fxmlUrl.openStream());
		final C controller = (C) loader.getController();
		
		return new FxmlLoaderResult<N, C>(node, controller);
	}
	
	public void showModalWindow(final String fxmlFileName, final String title, final Stage owner, final int width, final int height) throws IOException {
		final FxmlLoaderResult<Parent, Object> loaderResult = load(fxmlFileName);
		final Parent rootNode = loaderResult.getNode();
		final Scene scene = new Scene(rootNode, width, height);
		final Stage stage = new Stage();
		
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(owner);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.showAndWait();
	}
}