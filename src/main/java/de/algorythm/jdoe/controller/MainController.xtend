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
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javax.inject.Inject
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1
import org.slf4j.LoggerFactory
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import de.algorythm.jdoe.ui.jfx.cell.EntityCellValueFactory

public class MainController extends AbstractXtendController implements IController, IObserver, IEntityEditorManager {

	static val log = LoggerFactory.getLogger(typeof(MainController))
	
	@Inject extension IDAO dao
	@Inject extension JavaDbObjectEditorFacade facade
	@Inject extension GuiceFxmlLoader
	@FXML var ComboBox<EntityType> typeComboBox
	@FXML var TabPane tabs
	@FXML var Tab listTab
	@FXML var TableView<IEntity> entityList
	@FXML var TextField searchField
	@FXML var MenuButton newEntityButton
	var Schema schema
	var EntityType selectedType = EntityType.ALL
	val tabMap = new HashMap<String, TabData>
	var String searchPhrase
	
	override init() {
		schema = dao.schema
		
		searchField.textProperty.changeListener[
			searchPhrase = it
			
			runLater [|
				updateTableData
				showListTab
			]
		]
		
		entityList.setRowFactory(new EntityRow.Factory [
			showEntityEditor
		])
		
		typeComboBox.valueProperty.changeListener [
			setSelectedType
		]
		
		setupTypeSelection
		setupNewEntityButton
		
		addObserver(this)
		
		runLater [|
			searchField.requestFocus
		]
	}
	
	def SimpleStringProperty searchValueProperty() {
		searchValueProperty
	}
	
	def private void setSelectedType(EntityType type) {
		selectedType = if (type == null)
				EntityType.ALL
			else
				type
		
		updateTableColumns
		showListTab
	}
	
	def private void showListTab() {
		tabs.selectionModel.select(listTab)
	}
	
	def private void setupTypeSelection() {
		val types = new LinkedList<EntityType>(schema.types)
		
		types.addFirst(EntityType.ALL)
		
		typeComboBox.items.all = types
		typeComboBox.selectionModel.selectFirst
	}
	
	def private void setupNewEntityButton() {
		val menuItems = new LinkedList<MenuItem>
		
		for (type : schema.types) {
			val menuItem = new MenuItem
			
			menuItem.text = type.label
			
			menuItem.actionListener[|
				type.createEntity.showEntityEditor
			]
			
			menuItems += menuItem
		}
		
		newEntityButton.items.all = menuItems
	}
	
	def private void updateTableColumns() {
		val columns = new LinkedList<TableColumn<IEntity, ?>>
		
		if (selectedType == EntityType.ALL) {
			val column = new TableColumn<IEntity, String>('Entity')
			
			column.cellValueFactory = new EntityCellValueFactory
			columns += column
		} else {	
			for (Property property : selectedType.properties) {
				val column = new TableColumn<IEntity, String>(property.label)
				
				column.cellValueFactory = new PropertyCellValueFactory(property.index)
				columns += column
			}
		}
		
		update
		entityList.columns.all = columns
	}
	
	override update() {
		updateTableData
		updateTabs
	}
	
	def private updateTableData() {
		val entities = dao.list(selectedType, searchPhrase)
		
		entityList.items.all = entities
		listTab.text = '''Results («entities.size»)'''
	}
	
	def private updateTabs() {
		for (item : tabMap.values)
			item.tab.text = item.controller.label
	}
	
	def openDatabase() {
		// TODO
	}
	
	def showTypeEditor() throws IOException {
		facade.showTypeEditor
	}
	
	def private showEntityEditor(IEntity entity) {
		showEntityEditor(entity, null)
	}
	
	override showEntityEditor(IEntity entity, Procedure1<IEntity> saveCallback) {
		val entityId = entity.id
		val existingTabData = tabMap.get(entityId)
		
		if (existingTabData == null) { // create tab
			val loaderResult = <Node, EntityEditorController>load("/fxml/entity_editor.fxml")
		
			loaderResult.controller.init(entity, this, saveCallback)
			
			val tab = new Tab(loaderResult.controller.label)
			
			tab.content = loaderResult.node
			
			tabs.tabs += tab
			
			val tabData = new TabData(tab, loaderResult.controller)
			
			tabMap.put(entityId, tabData)
			
			tab.onClosedListener[|
				tabMap.remove(entityId)
				
				loaderResult.controller.close
			]
			
			tabs.selectionModel.select(tab)
		} else // focus existing tab
			tabs.selectionModel.select(existingTabData.tab)
	}
	
	override closeEntityEditor(IEntity entity) {
		val existingTab = tabMap.get(entity.id)
		
		if (existingTab != null) {
			val tab = existingTab.tab
			
			tab.onClosed.handle(null)
			tabs.tabs -= tab
		}
	}
}
