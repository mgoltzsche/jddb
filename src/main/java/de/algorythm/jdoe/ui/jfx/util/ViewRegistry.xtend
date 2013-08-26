package de.algorythm.jdoe.ui.jfx.util;

import de.algorythm.jdoe.controller.EntityEditorController
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.ViewData
import java.util.HashMap
import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javax.inject.Inject
import javax.inject.Singleton
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

@Singleton
public class ViewRegistry implements IEntityEditorManager {
	
	@Inject extension GuiceFxmlLoader
	val viewMap = new HashMap<String, ViewData>
	var TabPane tabPane
	
	def void setTabPane(TabPane tabPane) {
		this.tabPane = tabPane
	}
	
	override showEntityEditor(FXEntityReference entityRef) {
		showEntityEditor(entityRef, null)
	}
	
	override showEntityEditor(FXEntityReference entityRef, Procedure1<FXEntity> saveCallback) {
		val entityId = entityRef.id
		val existingViewData = viewMap.get(entityId)
		
		if (existingViewData == null) { // create tab
			val loaderResult = <Node, EntityEditorController>load('/fxml/entity_editor.fxml')
			
			val tab = new Tab
			
			loaderResult.controller.init(tab.textProperty, entityRef, saveCallback)
			
			tab.content = loaderResult.node
			
			tabPane.tabs += tab
			
			val viewData = new ViewData(tab, entityRef, loaderResult.controller)
			
			viewMap.put(entityId, viewData)
			
			tab.setOnClosed [
				viewMap.remove(entityId)
				
				loaderResult.controller.close
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