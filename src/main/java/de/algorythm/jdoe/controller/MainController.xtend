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
import java.io.IOException
import java.util.LinkedList
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javax.inject.Inject
import org.slf4j.LoggerFactory

public class MainController extends AbstractController implements IController, IObserver {

	static val log = LoggerFactory.getLogger(typeof(MainController))
	
	@Inject extension IDAO dao
	@Inject extension JavaDbObjectEditorFacade facade
	@FXML var ComboBox<EntityType> typeComboBox
	@FXML var TabPane tabs
	@FXML var Tab listTab
	@FXML var TableView<IEntity> entityList
	var Schema schema
	var EntityType selectedType
	
	override init() {
		schema = dao.schema
		
		entityList.setRowFactory(new EntityRow.Factory [
			try {
				val entityLabel = toString
				val tabLabel = '''«type.label»«IF entityLabel != null»: «entityLabel»«ENDIF»'''
				
				showEntityEditorTab(tabLabel)
			} catch (IOException e) {
				log.error('Cannot open entity editor', e)
			}
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
		val entities = dao.list(selectedType)
		
		entityList.items.all = entities
		listTab.text = '''Results («entities.size»)'''
	}
	
	def openDatabase() {
		// TODO
	}
	
	def showTypeEditor() throws IOException {
		facade.showTypeEditor
	}
	
	def createEntity() {
		if (selectedType != EntityType.DEFAULT)
			selectedType.createEntity.showEntityEditorTab(selectedType.label + ' (neu)')
	}
	
	def private showEntityEditorTab(IEntity entity, String tabLabel) {
		val existingTab = entity.existingTab
		
		if (existingTab == null) { // create tab
			val tab = new Tab(tabLabel)
			tab.id = entity.tabId
			
			entity.showEntityEditor(tab)
			
			tabs.tabs += tab
			
			tabs.selectionModel.select(tab)
		} else // focus existing tab
			tabs.selectionModel.select(existingTab)
	}
	
	def private Tab getExistingTab(IEntity entity) {
		val tabId = entity.tabId
		
		for (tab : tabs.tabs)
			if (tab.id == tabId)
				return tab
	}
	
	def private tabId(IEntity entity) {
		val entityId = entity.id
		
		if (entityId == null)
			entity.type.name
		else
			entityId
	}
}
