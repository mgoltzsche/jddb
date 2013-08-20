package de.algorythm.jdoe.ui.jfx.util;

import de.algorythm.jdoe.controller.EntityEditorController
import de.algorythm.jdoe.model.dao.IDAO
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
import org.slf4j.LoggerFactory

import static javafx.application.Platform.*
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue

@Singleton
public class ViewRegistry implements IEntityEditorManager {

	static val LOG = LoggerFactory.getLogger(typeof(ViewRegistry))

	@Inject extension GuiceFxmlLoader
	@Inject extension IDAO<FXEntityReference,IFXPropertyValue<?>,FXEntity>
	val viewMap = new HashMap<String, ViewData>
	var TabPane tabPane
	
	def void setTabPane(TabPane tabPane) {
		this.tabPane = tabPane
	}
	
	override showEntityEditor(FXEntity entity) {
		showEntityEditor(entity, null)
	}
	
	override showEntityEditor(FXEntityReference entityRef, Procedure1<FXEntity> saveCallback) {
		runTask [|
			try {
				val entity = entityRef.find
				
				runLater [|
					entity.showEntityEditor(saveCallback)
				]
			} catch(IllegalArgumentException e) {
				LOG.debug('''Cannot open entity editor for «entityRef»(«entityRef») because the entity does not exist''')
			}
		]
	}
	
	override showEntityEditor(FXEntity entity, Procedure1<FXEntity> saveCallback) {
		val entityId = entity.id
		val existingViewData = viewMap.get(entityId)
		
		if (existingViewData == null) { // create tab
			val loaderResult = <Node, EntityEditorController>load('/fxml/entity_editor.fxml')
			
			loaderResult.controller.init(entity, saveCallback)
			
			val tab = new Tab
			
			tab.content = loaderResult.node
			
			tabPane.tabs += tab
			
			val viewData = new ViewData(tab, entity, loaderResult.controller)
			
			viewMap.put(entityId, viewData)
			
			tab.textProperty.bind(entity.labelProperty)
			
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