package de.algorythm.jddb

import de.algorythm.jddb.bundle.Bundle
import de.algorythm.jddb.cache.IObjectCache
import de.algorythm.jddb.controller.IEntitySaveResult
import de.algorythm.jddb.controller.MainController
import de.algorythm.jddb.model.dao.IDAO
import de.algorythm.jddb.model.meta.MEntityType
import de.algorythm.jddb.taskQueue.ITaskPriority
import de.algorythm.jddb.ui.jfx.controls.FXEntityDetailPopup
import de.algorythm.jddb.ui.jfx.loader.fxml.FxmlLoaderResult
import de.algorythm.jddb.ui.jfx.loader.fxml.GuiceFxmlLoader
import de.algorythm.jddb.ui.jfx.model.FXEntity
import de.algorythm.jddb.ui.jfx.model.FXEntityReference
import de.algorythm.jddb.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jddb.ui.jfx.taskQueue.FXTaskQueue
import de.algorythm.jddb.ui.jfx.util.IEntityEditorManager
import java.io.File
import java.io.IOException
import java.util.Collection
import java.util.Date
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javax.inject.Inject
import javax.inject.Singleton
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

import static javafx.application.Platform.*
import de.algorythm.jddb.ui.jfx.dialogs.ConfirmDialog

@Singleton
public class JddbFacade {

	static val TYPE_EDITOR_FXML = '/fxml/type_editor.fxml'

	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@Inject extension FXTaskQueue taskQueue
	@Inject IObjectCache<FXEntity> entityCache
	@Inject extension GuiceFxmlLoader
	@Inject IEntityEditorManager editorManager
	@Inject extension ConfirmDialog
	@Inject Bundle bundle
	var FXEntityDetailPopup entityDetailsPopup
	var MainController mainController
	var Stage primaryStage
	
	def startApplication(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage
		entityDetailsPopup = new FXEntityDetailPopup
		val FxmlLoaderResult<Parent, MainController> loaderResult = load('/fxml/jddb.fxml')
		
		mainController = loaderResult.controller
		
		primaryStage.setOnCloseRequest [
			if (!editorManager.allSaved)
				if (!confirm(bundle.confirmCloseUnsaved))
					consume
		]
		
		primaryStage.title = 'Java Desktop Database'
		primaryStage.scene = new Scene(loaderResult.node, 900, 700)
		primaryStage.minWidth = 300
		primaryStage.minHeight = 400
		primaryStage.show
		primaryStage.centerOnScreen
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
		if ((!dbFile.exists || dbFile.directory && dbFile.list.length == 0) &&
				!confirm(dbFile.absolutePath + ' ' + bundle.confirmDatabaseCreation)) {
			try {
				dbFile.delete
			} catch(IOException e) {}
			return
		}
		
		editorManager.closeAll
		
		val closeTask = closeDB
		
		runTask('open-db', bundle.taskOpenDB, ITaskPriority.LAST) [|
			closeTask.requireCompleted
			dao.open(dbFile)
			
			runLater [|
				mainController.onDatabaseOpened
			]
		]
	}
	
	def private closeDB() {
		mainController.onDatabaseClose
		
		runTask('close-db', bundle.taskCloseDB, ITaskPriority.LAST) [|
			entityCache.clear
			
			if (dao.opened)
				dao.close
		]
	}
	
	def showTypeEditor() throws IOException {
		showModalWindow(TYPE_EDITOR_FXML, bundle.settings, primaryStage, 600, 500)
	}
	
	def updateSchema(Collection<MEntityType> types) {
		mainController.onDatabaseClose
		
		val updateSchemaTask = runTask('update-schema-' + new Date().time, bundle.updateDatabaseSchema, ITaskPriority.LAST) [|
			types.updateSchemaTypes
			reloadAll
		]
		
		runTask('rebuild-db-index', bundle.rebuildSearchIndex, ITaskPriority.LAST) [|
			updateSchemaTask.requireCompleted
			rebuildIndex
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