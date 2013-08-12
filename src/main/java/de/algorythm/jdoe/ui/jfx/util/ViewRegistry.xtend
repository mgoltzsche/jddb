package de.algorythm.jdoe.ui.jfx.util;

import de.algorythm.jdoe.controller.EntityEditorController
import de.algorythm.jdoe.model.entity.IEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.ViewData
import java.util.HashMap
import java.util.LinkedList
import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javax.inject.Inject
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

public class ViewRegistry implements IEntityEditorManager {

	@Inject extension GuiceFxmlLoader
	val viewMap = new HashMap<String, ViewData>
	var TabPane tabPane
	
	def void setTabPane(TabPane tabPane) {
		this.tabPane = tabPane
	}
	
	override showEntityEditor(FXEntity entity) {
		showEntityEditor(entity, null)
	}
	
	override showEntityEditor(FXEntity entity, Procedure1<FXEntity> saveCallback) {
		val entityId = entity.id
		val existingViewData = viewMap.get(entityId)
		
		if (existingViewData == null) { // create tab
			val loaderResult = <Node, EntityEditorController>load('/fxml/entity_editor.fxml')
		
			loaderResult.controller.init(entity, this, saveCallback)
			
			val tab = new Tab()
			
			tab.content = loaderResult.node
			
			tabPane.tabs += tab
			
			val viewData = new ViewData(tab, entity, loaderResult.controller)
			
			viewMap.put(entityId, viewData)
			
			tab.textProperty.bind(entity.label)
			
			tab.setOnClosed [
				viewMap.remove(entityId)
				
				loaderResult.controller.close
			]
			
			tabPane.selectionModel.select(tab)
		} else // focus existing tab
			tabPane.selectionModel.select(existingViewData.tab)
	}
	
	override closeEntityEditor(FXEntity entity) {
		val existingTab = viewMap.get(entity.id)
		
		if (existingTab != null) {
			val tab = existingTab.tab
			
			tab.onClosed.handle(null)
			tab.textProperty.unbind
			
			tabPane.tabs -= tab
		}
	}
	
	override wrap(IEntity entity) {
		if (entity == null)
			return null
		
		val view = viewMap.get(entity.id)
		
		return if (view == null)
			new FXEntity(entity)
		else
			view.entity
	}
	
	override wrap(Iterable<IEntity> entities) {
		val fxEntities = new LinkedList<FXEntity>
		
		for (entity : entities)
			fxEntities += wrap(entity)
		
		fxEntities
	}
	
}