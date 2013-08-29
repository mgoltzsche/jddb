package de.algorythm.jdoe

import de.algorythm.jdoe.bundle.Bundle
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue
import de.algorythm.jdoe.ui.jfx.util.FxmlLoaderResult
import de.algorythm.jdoe.ui.jfx.util.GuiceFxmlLoader
import java.io.IOException
import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javax.inject.Inject

public class JavaDbObjectEditorFacade {

	@Inject FXTaskQueue taskQueue
	@Inject Bundle bundle
	@Inject IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@Inject extension GuiceFxmlLoader
	
	def startApplication(Stage primaryStage) throws IOException {
		taskQueue.runTask('open-db', bundle.taskOpenDB) [|
			dao.open
			
			Platform.runLater [|
				val FxmlLoaderResult<Parent, Object> loaderResult = '/fxml/entity_list.fxml'.load
				
				primaryStage.title = 'jDOE'
				primaryStage.scene = new Scene(loaderResult.node, 600, 500)
				primaryStage.show
			]
		]
	}
	
	def stopApplication() throws IOException {
		taskQueue.runTask('close-db', bundle.taskCloseDB) [|
			dao.close
			taskQueue.close
		]
	}
	
	def showTypeEditor() throws IOException {
		showWindow('/fxml/type_editor.fxml', 'jDOE - ' + bundle.typeDefinition, 500, 400)
	}
}