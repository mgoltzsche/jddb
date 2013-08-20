package de.algorythm.jdoe

import de.algorythm.jdoe.controller.IController
import de.algorythm.jdoe.controller.NotificationController
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.taskQueue.TaskQueue
import de.algorythm.jdoe.ui.jfx.util.FxmlLoaderResult
import de.algorythm.jdoe.ui.jfx.util.GuiceFxmlLoader
import java.io.IOException
import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javax.inject.Inject
import de.algorythm.jdoe.bundle.Bundle
import de.algorythm.jdoe.ui.jfx.model.FXEntity

public class JavaDbObjectEditorFacade {

	@Inject TaskQueue taskQueue
	@Inject Bundle bundle
	@Inject IDAO<FXEntity> dao
	@Inject extension GuiceFxmlLoader
	
	def startApplication(Stage primaryStage) throws IOException {
		taskQueue.notifier = new NotificationController(primaryStage)
		
		taskQueue.runTask('open db') [|
			dao.open
			
			Platform.runLater [|
				val FxmlLoaderResult<Parent, IController> loaderResult = '/fxml/entity_list.fxml'.load
				
				primaryStage.title = 'jDOE'
				primaryStage.scene = new Scene(loaderResult.node, 600, 500)
				primaryStage.show
			]
		]
	}
	
	def stopApplication() throws IOException {
		taskQueue.runTask('close db') [|
			dao.close
			taskQueue.close
		]
	}
	
	def showTypeEditor() throws IOException {
		showWindow('/fxml/type_editor.fxml', 'jDOE - ' + bundle.typeDefinition, 500, 400)
	}
}