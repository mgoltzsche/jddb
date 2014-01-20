package de.algorythm.jdoe

import de.algorythm.jdoe.bundle.Bundle
import de.algorythm.jdoe.cache.IObjectCache
import de.algorythm.jdoe.controller.IEntitySaveResult
import de.algorythm.jdoe.controller.MainController
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.taskQueue.ITaskPriority
import de.algorythm.jdoe.taskQueue.TaskState
import de.algorythm.jdoe.ui.jfx.controls.FXEntityDetailPopup
import de.algorythm.jdoe.ui.jfx.loader.fxml.FxmlLoaderResult
import de.algorythm.jdoe.ui.jfx.loader.fxml.GuiceFxmlLoader
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue
import de.algorythm.jdoe.ui.jfx.util.IEntityEditorManager
import java.io.File
import java.io.IOException
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javax.inject.Inject
import javax.inject.Singleton
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

import static javafx.application.Platform.*
import java.util.Collection
import de.algorythm.jdoe.model.meta.MEntityType
import java.util.Date

@Singleton
public class JavaDbObjectEditorFacade {

	static val TYPE_EDITOR_FXML = '/fxml/type_editor.fxml'

	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@Inject extension FXTaskQueue taskQueue
	@Inject IObjectCache<FXEntity> entityCache
	@Inject extension GuiceFxmlLoader
	@Inject IEntityEditorManager editorManager
	@Inject Bundle bundle
	val FXEntityDetailPopup entityDetailsPopup = new FXEntityDetailPopup
	var MainController mainController
	var Stage primaryStage
	
	def startApplication(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage
		val FxmlLoaderResult<Parent, MainController> loaderResult = load('/fxml/jdoe.fxml')
		
		mainController = loaderResult.controller
		
		primaryStage.title = 'jDOE'
		primaryStage.scene = new Scene(loaderResult.node, 900, 700)
		primaryStage.minWidth = 300
		primaryStage.minHeight = 400
		primaryStage.show
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
	
	def closeAllEntityEditors() {
		editorManager.closeAll
	}
	
	def void openDB(File dbFile) {
		editorManager.closeAll
		
		val closeTask = closeDB
		
		runTask('open-db', bundle.taskOpenDB, ITaskPriority.LOWER) [|
			if (closeTask.state != TaskState.FAILED) {
				dao.open(dbFile)
				
				runLater [|
					mainController.onDatabaseOpened
				]
			}
		]
	}
	
	def private closeDB() {
		mainController.onDatabaseClose
		
		runTask('close-db', bundle.taskCloseDB, ITaskPriority.LOWER) [|
			if (dao.opened)
				dao.close
		]
	}
	
	def showTypeEditor() throws IOException {
		showModalWindow(TYPE_EDITOR_FXML, 'jDOE - ' + bundle.typeDefinition, primaryStage, 600, 500)
	}
	
	def updateSchema(Collection<MEntityType> types) {
		runTask('update-schema-' + new Date().time, 'Update Database schema', ITaskPriority.LOWER) [|
			types.updateSchemaTypes
			reloadAll
			
			//runTask('rebuild-index', 'Rebuild index', ITaskPriority.LOWER) [|
				rebuildIndex
			//]
		]
	}
	
	def private reloadAll() {
		val openedEditorIDs = editorManager.openEditorIDs
		
		entityCache.clear
		
		runLater [|
			editorManager.closeAll
			mainController.onReload
		]
		
		for (id : openedEditorIDs) {
			try {
				val entity = id.find
				
				runLater [|
					editorManager.showEntityEditor(entity)
				]
			} catch(IllegalArgumentException e) {
			}
		}
	}
}