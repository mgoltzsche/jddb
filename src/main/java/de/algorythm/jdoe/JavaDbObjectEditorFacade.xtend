package de.algorythm.jdoe

import de.algorythm.jdoe.bundle.Bundle
import de.algorythm.jdoe.controller.IEntitySaveResult
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.meta.MEntityType
import de.algorythm.jdoe.ui.jfx.loader.fxml.FxmlLoaderResult
import de.algorythm.jdoe.ui.jfx.loader.fxml.GuiceFxmlLoader
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue
import de.algorythm.jdoe.ui.jfx.util.IEntityEditorManager
import java.io.File
import java.io.IOException
import java.util.Collection
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javax.inject.Inject
import javax.inject.Singleton
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

import static javafx.application.Platform.*
import de.algorythm.jdoe.ui.jfx.controls.FXEntityDetailPopup
import javafx.scene.Node
import de.algorythm.jdoe.taskQueue.ITaskPriority
import de.algorythm.jdoe.taskQueue.TaskState

@Singleton
public class JavaDbObjectEditorFacade {

	static val TYPE_EDITOR_FXML = '/fxml/type_editor.fxml'

	@Inject extension FXTaskQueue taskQueue
	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@Inject extension GuiceFxmlLoader
	@Inject IEntityEditorManager editorManager
	@Inject Bundle bundle
	val FXEntityDetailPopup entityDetailsPopup = new FXEntityDetailPopup
	
	def startApplication(Stage primaryStage) throws IOException {
		runLater [|
			val FxmlLoaderResult<Parent, Object> loaderResult = load('/fxml/jdoe.fxml')
			
			primaryStage.title = 'jDOE'
			primaryStage.scene = new Scene(loaderResult.node, 900, 700)
			primaryStage.minWidth = 300
			primaryStage.minHeight = 400
			primaryStage.show
		]
	}
	
	def stopApplication() {
		closeDB
	}
	
	def showEntityDetails(FXEntityReference entityRef, Node node) {
		entityDetailsPopup.show(node, entityRef)
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
	
	def void openDB(File dbFile, Procedure1<Collection<MEntityType>> callback) {
		val closeTask = closeDB
		
		runTask('open-db', bundle.taskOpenDB, ITaskPriority.LOWER) [|
			if (closeTask.state != TaskState.FAILED) {
				dao.open(dbFile)
				
				runLater [|
					callback.apply(dao.schema.types)
				]
			}
		]
	}
	
	def private closeDB() {
		runTask('close-db', bundle.taskCloseDB, ITaskPriority.LOWER) [|
			if (dao.opened) {
				dao.close
			}
		]
	}
	
	def showTypeEditor() throws IOException {
		showWindow(TYPE_EDITOR_FXML, 'jDOE - ' + bundle.typeDefinition, 600, 500)
	}
}