package de.algorythm.jdoe.ui.jfx.util;

import de.algorythm.jdoe.controller.EntityEditorController
import de.algorythm.jdoe.controller.IEntitySaveResult
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.ViewData
import java.util.HashMap
import javafx.scene.Node
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Region
import javax.inject.Inject
import javax.inject.Singleton
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1
import static javafx.application.Platform.*

@Singleton
public class ViewRegistry implements IEntityEditorManager {
	
	static val EDITOR_FXML = '/fxml/entity_editor.fxml'
	
	@Inject extension GuiceFxmlLoader
	val viewMap = new HashMap<String, ViewData>
	var TabPane tabPane
	
	def void setTabPane(TabPane tabPane) {
		this.tabPane = tabPane
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
			
			tab.textProperty.bind(stateModel.titleProperty)
			progressIndicator.visibleProperty.bind(stateModel.busyProperty)
			
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
			
			tabPane.selectionModel.select(tab)
		} else // focus existing tab
			tabPane.selectionModel.select(existingViewData.tab)
	}
	
	override closeEntityEditor(FXEntityReference entity) {
		val existingTab = viewMap.get(entity.id)
		
		if (existingTab != null) {
			val tab = existingTab.tab
			
			tab.onClosed.handle(null)
			tab.textProperty.unbind
			
			tabPane.tabs -= tab
		}
	}
}