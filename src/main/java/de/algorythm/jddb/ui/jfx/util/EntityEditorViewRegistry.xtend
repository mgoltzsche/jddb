package de.algorythm.jddb.ui.jfx.util;

import de.algorythm.jddb.controller.EntityEditorController
import de.algorythm.jddb.controller.IEntitySaveResult
import de.algorythm.jddb.ui.jfx.loader.fxml.GuiceFxmlLoader
import de.algorythm.jddb.ui.jfx.model.FXEntityReference
import de.algorythm.jddb.ui.jfx.model.ViewData
import java.util.LinkedHashMap
import java.util.LinkedList
import javafx.scene.Node
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Region
import javax.inject.Inject
import javax.inject.Singleton
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

import static javafx.application.Platform.*
import java.util.ArrayList

@Singleton
public class EntityEditorViewRegistry implements IEntityEditorManager {
	
	static val EDITOR_FXML = '/fxml/entity_editor.fxml'
	
	@Inject extension GuiceFxmlLoader
	val viewMap = new LinkedHashMap<String, ViewData>
	var TabPane tabPane
	
	def void setTabPane(TabPane tabPane) {
		this.tabPane = tabPane
	}
	
	override getOpenEditorIDs() {
		val ids = new LinkedList<String>
		
		for (id : viewMap.keySet)
			ids += id
		
		ids
	}
	
	override showEntityEditor(FXEntityReference entityRef) {
		showEntityEditor(entityRef, null)
	}
	
	override showEntityEditor(FXEntityReference entityRef, Procedure1<IEntitySaveResult> saveCallback) {
		val entityId = entityRef.id
		val existingViewData = viewMap.get(entityId)
		
		if (existingViewData == null) { // create tab
			val loaderResult = <Node, EntityEditorController>load(EDITOR_FXML)
			val controller = loaderResult.controller
			val tab = new Tab
			val progressIndicator = new ProgressIndicator
			
			progressIndicator.setPrefSize(17, 17)
			progressIndicator.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
			tab.graphic = progressIndicator
			
			val stateModel = controller.init(entityRef, saveCallback)
			
			progressIndicator.visibleProperty.bind(stateModel.busyProperty)
			tab.textProperty.bind(stateModel.titleProperty)
			stateModel.pristineProperty.addListener [
				if (stateModel.pristine)
					tab.styleClass -= 'unsaved'
				else
					tab.styleClass += 'unsaved'
			]
			
			tab.content = loaderResult.node
			
			tabPane.tabs += tab
			
			val viewData = new ViewData(tab, entityRef, controller)
			
			viewMap.put(entityId, viewData)
			
			tab.setOnClosed [
				viewMap.remove(entityId)
				controller.close
				// gc workaround:
				runLater [|
					tab.content = null
					tab.onClosed = null
				]
			]
			
			runLater [|
				tabPane.selectionModel.select(tab)
			]
		} else {// focus existing tab
			runLater [|
				tabPane.selectionModel.select(existingViewData.tab)
			]
		}
	}
	
	override closeEntityEditor(FXEntityReference entityRef) {
		val existingTab = viewMap.get(entityRef.id)
		
		if (existingTab != null) {
			val tab = existingTab.tab
			
			tab.onClosed.handle(null)
			tab.textProperty.unbind
			
			tabPane.tabs -= tab
		}
	}
	
	override closeAll() {
		for (viewData : new ArrayList(viewMap.values))
			viewData.entityRef.closeEntityEditor
	}
}