package de.algorythm.jdoe

import de.algorythm.jdoe.bundle.Bundle
import de.algorythm.jdoe.controller.IEntitySaveResult
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.meta.EntityType
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue
import de.algorythm.jdoe.ui.jfx.loader.fxml.FxmlLoaderResult
import de.algorythm.jdoe.ui.jfx.loader.fxml.GuiceFxmlLoader
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
import javafx.scene.input.MouseEvent
import javafx.event.EventHandler

@Singleton
public class JavaDbObjectEditorFacade {

	static val TYPE_EDITOR_FXML = '/fxml/type_editor.fxml'

	@Inject extension FXTaskQueue taskQueue
	@Inject extension GuiceFxmlLoader
	@Inject IEntityEditorManager editorManager
	@Inject Bundle bundle
	@Inject IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	var Stage stage
	var FXEntityDetailPopup entityPopup = new FXEntityDetailPopup
	val EventHandler<? super MouseEvent> mouseExitHandler = [
		entityPopup.hide
	]
	
	def startApplication(Stage primaryStage) throws IOException {
		this.stage = primaryStage
		
		runLater [|
			val FxmlLoaderResult<Parent, Object> loaderResult = load('/fxml/jdoe.fxml')
			
			stage.title = 'jDOE'
			stage.scene = new Scene(loaderResult.node, 900, 700)
			stage.minWidth = 300
			stage.minHeight = 400
			stage.show
		]
	}
	
	def stopApplication() {
		closeDB
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
	
	def showEntityDetailPopup(FXEntity entity, Node node) {
		val pos = node.localToScene(0, 0)
		entityPopup.entity = entity
		entityPopup.x = stage.x + pos.x + 3
		entityPopup.y = stage.y + pos.y + 3
		
		entityPopup.show(stage)
		
		node.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseExitHandler)
		node.addEventHandler(MouseEvent.MOUSE_EXITED, mouseExitHandler)
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
			if (dao.opened) {
				dao.close
			}
		]
	}
	
	def showTypeEditor() throws IOException {
		showWindow(TYPE_EDITOR_FXML, 'jDOE - ' + bundle.typeDefinition, 500, 400)
	}
}