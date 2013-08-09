package de.algorythm.jdoe.ui.jfx.util;

import java.util.HashMap;

import de.algorythm.jdoe.ui.jfx.model.ViewData;
import de.algorythm.jdoe.controller.IEntityEditorManager
import de.algorythm.jdoe.model.entity.IEntity
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1
import javafx.scene.control.TabPane
import javafx.scene.Node
import de.algorythm.jdoe.controller.EntityEditorController
import javafx.scene.control.Tab
import de.algorythm.jdoe.controller.AbstractXtendController
import javax.inject.Inject

public class ViewRegistry extends AbstractXtendController implements IEntityEditorManager {

	@Inject extension GuiceFxmlLoader
	val viewMap = new HashMap<String, ViewData>
	var TabPane tabPane
	
	def void setTabPane(TabPane tabPane) {
		this.tabPane = tabPane
	}
	
	def Iterable<ViewData> getViews() {
		viewMap.values
	}
	
	override showEntityEditor(IEntity entity) {
		showEntityEditor(entity, null)
	}
	
	override showEntityEditor(IEntity entity, Procedure1<IEntity> saveCallback) {
		val entityId = entity.id
		val existingViewData = viewMap.get(entityId)
		
		if (existingViewData == null) { // create tab
			val loaderResult = <Node, EntityEditorController>load('/fxml/entity_editor.fxml')
		
			loaderResult.controller.init(entity, this, saveCallback)
			
			val tab = new Tab(loaderResult.controller.label)
			
			tab.content = loaderResult.node
			
			tabPane.tabs += tab
			
			val viewData = new ViewData(tab, loaderResult.controller)
			
			viewMap.put(entityId, viewData)
			
			tab.onClosedListener[|
				viewMap.remove(entityId)
				
				loaderResult.controller.close
			]
			
			tabPane.selectionModel.select(tab)
		} else // focus existing tab
			tabPane.selectionModel.select(existingViewData.tab)
	}
	
	override closeEntityEditor(IEntity entity) {
		val existingTab = viewMap.get(entity.id)
		
		if (existingTab != null) {
			val tab = existingTab.tab
			
			tab.onClosed.handle(null)
			tabPane.tabs -= tab
		}
	}
}