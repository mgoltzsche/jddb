package de.algorythm.jdoe.controller

import de.algorythm.jdoe.JavaDbObjectEditorFacade
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.dao.IObserver
import de.algorythm.jdoe.model.entity.IEntity
import de.algorythm.jdoe.model.meta.EntityType
import de.algorythm.jdoe.model.meta.Property
import de.algorythm.jdoe.model.meta.Schema
import de.algorythm.jdoe.ui.jfx.cell.EntityRow
import de.algorythm.jdoe.ui.jfx.cell.PropertyCellValueFactory
import de.algorythm.jdoe.ui.jfx.util.GuiceFxmlLoader
import java.io.IOException
import java.util.HashMap
import java.util.LinkedList
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javax.inject.Inject
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1
import org.slf4j.LoggerFactory

public class MainController extends AbstractXtendController implements IController, IObserver, IEntityEditorManager {

	static val log = LoggerFactory.getLogger(typeof(MainController))
	
	@Inject extension IDAO dao
	@Inject extension JavaDbObjectEditorFacade facade
	@Inject extension GuiceFxmlLoader
	@FXML var ComboBox<EntityType> typeComboBox
	@FXML var TabPane tabs
	@FXML var Tab listTab
	@FXML var TableView<IEntity> entityList
	var Schema schema
	var EntityType selectedType
	val tabMap = new HashMap<Object, TabData>
	var tabSequence = 0
	
	override init() {
		schema = dao.schema
		
		entityList.setRowFactory(new EntityRow.Factory [
			showEntityEditor
		])
		
		typeComboBox.valueProperty.changeListener [
			setSelectedType
		]
		
		setupTypeSelection
		
		addObserver(this)
	}
	
	def private void setSelectedType(EntityType type) {
		selectedType = if (type == null)
				EntityType.DEFAULT
			else
				type
		
		updateTableColumns
		tabs.selectionModel.select(listTab)
	}
	
	def private void setupTypeSelection() {
		typeComboBox.items.all = schema.types
		typeComboBox.selectionModel.selectFirst
	}
	
	def private void updateTableColumns() {
		val columns = new LinkedList<TableColumn<IEntity, ?>>
		
		for (Property property : selectedType.getProperties()) {
			val column = new TableColumn<IEntity, String>(property.label)
			
			column.cellValueFactory = new PropertyCellValueFactory(property.index)
			columns += column
		}
		
		update
		entityList.columns.all = columns
	}
	
	override update() {
		// update table data
		val entities = dao.list(selectedType)
		
		entityList.items.all = entities
		listTab.text = '''Results («entities.size»)'''
		
		// update tabs
		for (item : tabMap.values)
			item.tab.text = item.controller.label
	}
	
	def openDatabase() {
		// TODO
	}
	
	def showTypeEditor() throws IOException {
		facade.showTypeEditor
	}
	
	def createEntity() {
		if (selectedType != EntityType.DEFAULT)
			selectedType.createEntity.showEntityEditor
	}
	
	def private showEntityEditor(IEntity entity) {
		showEntityEditor(entity, null)
	}
	
	override showEntityEditor(IEntity entity, Procedure1<IEntity> saveCallback) {
		val entityId = entity.id
		val existingTabData = if (entityId == null)
				null
			else
				tabMap.get(entityId)
		val tabId = if (entityId == null)
				'tab' + (tabSequence = tabSequence + 1)
			else
				entityId
		
		if (existingTabData == null) { // create tab
			val loaderResult = <Node, EntityEditorController>load("/fxml/entity_editor.fxml");
		
			loaderResult.controller.init(entity, this, saveCallback)
			
			val tab = new Tab(loaderResult.controller.label)
			
			tab.content = loaderResult.node
			
			tabs.tabs += tab
			
			val tabData = new TabData(tab, loaderResult.controller)
			
			tabMap.put(tabId, tabData)
			
			tab.onClosedListener[|
				tabMap.remove(tabId)
				
				loaderResult.controller.close
			]
			
			tabs.selectionModel.select(tab)
		} else // focus existing tab
			tabs.selectionModel.select(existingTabData.tab)
		
		tabId
	}
	
	override closeEntityEditor(IEntity entity) {
		closeEntityEditor(entity.id)
	}
	
	override closeEntityEditor(Object viewId) {
		val existingTab = tabMap.get(viewId)
		
		if (existingTab != null) {
			val tab = existingTab.tab
			
			tab.onClosed.handle(null)
			tabs.tabs -= tab
		}
	}
}
