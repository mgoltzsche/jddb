package de.algorythm.jdoe

import de.algorythm.jdoe.bundle.Bundle
import de.algorythm.jdoe.controller.IEntitySaveResult
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.meta.EntityType
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue
import de.algorythm.jdoe.ui.jfx.util.FxmlLoaderResult
import de.algorythm.jdoe.ui.jfx.util.GuiceFxmlLoader
import de.algorythm.jdoe.ui.jfx.util.IEntityEditorManager
import java.io.File
import java.io.IOException
import java.util.Collection
import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javax.inject.Inject
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

import static javafx.application.Platform.*

public class JavaDbObjectEditorFacade {

	@Inject extension FXTaskQueue taskQueue
	@Inject extension GuiceFxmlLoader
	@Inject IEntityEditorManager editorManager
	@Inject Bundle bundle
	@Inject IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	
	def startApplication(Stage primaryStage) throws IOException {
		Platform.runLater [|
			val FxmlLoaderResult<Parent, Object> loaderResult = load('/fxml/entity_list.fxml')
			
			primaryStage.title = 'jDOE'
			primaryStage.scene = new Scene(loaderResult.node, 800, 600)
			primaryStage.minWidth = 300
			primaryStage.minHeight = 400
			primaryStage.show
		]
	}
	
	def showEntityEditor(FXEntityReference entityRef) {
		editorManager.showEntityEditor(entityRef)
	}
	
	def showEntityEditor(FXEntityReference entityRef, Procedure1<IEntitySaveResult> saveCallback) {
		editorManager.showEntityEditor(entityRef, saveCallback)
	}
	
	def closeEntityEditor(FXEntityReference entityRef) {
		editorManager.closeEntityEditor(entityRef)
	}
	
	def void openDB(File dbFile, Procedure1<Collection<EntityType>> callback) {
		val closeTask = closeDB
		
		runTask('open-db', bundle.taskOpenDB) [|
			if (!closeTask.failed) {
				dao.open(dbFile)
				
				runLater [|
					callback.apply(dao.schema.types)
				]
			}
		]
	}
	
	def private closeDB() {
		runTask('close-db', bundle.taskCloseDB) [|
			if (dao.opened)
				dao.close
		]
	}
	
	def stopApplication() {
		closeDB
		taskQueue.close
	}
	
	def showTypeEditor() throws IOException {
		showWindow('/fxml/type_editor.fxml', 'jDOE - ' + bundle.typeDefinition, 500, 400)
	}
}