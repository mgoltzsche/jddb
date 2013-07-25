package de.algorythm.jdoe;

import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.jdoe.controller.EntityEditorController;
import de.algorythm.jdoe.controller.IController;
import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.ui.util.FxmlLoaderResult;
import de.algorythm.jdoe.ui.util.GuiceFxmlLoader;

public class JavaDbObjectEditorFacade {

	static private final Logger log = LoggerFactory.getLogger(JavaDbObjectEditorFacade.class);
	
	@Inject private GuiceFxmlLoader fxmlLoader;
	@Inject private IDAO dao;
	
	public void startApplication(final Stage stage) throws IOException {
		dao.open();
		showWindow("/fxml/entity_list.fxml", "jDOE", 600, 500);
	}
	
	public void stopApplication() throws IOException {
		dao.close();
	}
	
	public void showTypeEditor() throws IOException {
		showWindow("/fxml/type_editor.fxml", "jDOE - Data type definition", 500, 400);
	}
	
	public void showEntityEditor(final IEntity entity, final Tab tab) throws IOException {
		final FxmlLoaderResult<Node, EntityEditorController> loaderResult = fxmlLoader.load("/fxml/entity_editor.fxml");
		
		loaderResult.getController().init(entity, tab);
		tab.setContent(loaderResult.getNode());
	}
	
	private void showWindow(final String fxmlFileName, final String title, final int width, final int height) throws IOException {
		FxmlLoaderResult<Parent, IController> loaderResult = fxmlLoader.load(fxmlFileName);
		Parent rootNode = loaderResult.getNode();
		Scene scene = new Scene(rootNode, width, height);
		Stage stage = new Stage();
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
	}
}
