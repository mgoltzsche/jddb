package de.algorythm.jdoe.controller

import de.algorythm.jdoe.JavaDbObjectEditorFacade
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.dao.IObserver
import de.algorythm.jdoe.model.meta.EntityType
import de.algorythm.jdoe.model.meta.Property
import de.algorythm.jdoe.model.meta.Schema
import de.algorythm.jdoe.ui.jfx.cell.EntityCellValueFactory
import de.algorythm.jdoe.ui.jfx.cell.EntityRow
import de.algorythm.jdoe.ui.jfx.cell.PropertyCellValueFactory
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.util.ViewRegistry
import java.io.IOException
import java.util.LinkedList
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javax.inject.Inject
import org.slf4j.LoggerFactory

public class MainController extends AbstractXtendController implements IController, IObserver {
	
	static val log = LoggerFactory.getLogger(typeof(MainController))
	
	@Inject extension IDAO dao
	@Inject extension JavaDbObjectEditorFacade facade
	@Inject extension ViewRegistry
	@FXML var ComboBox<EntityType> typeComboBox
	@FXML var TabPane tabs
	@FXML var Tab listTab
	@FXML var TableView<FXEntity> entityList
	@FXML var TextField searchField
	@FXML var MenuButton newEntityButton
	var Schema schema
	var EntityType selectedType = EntityType.ALL
	var String searchPhrase
	
	override init() {
		tabPane = tabs
		
		schema = dao.schema
		
		searchField.textProperty.changeListener [
			searchPhrase = it
			
			runLater [|
				showListTab
				update
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
			
			menuItem.actionListener [|
				new FXEntity(type.createEntity).showEntityEditor
			]
			
			menuItems += menuItem
		}
		
		newEntityButton.items.all = menuItems
	}
	
	def private void updateTableColumns() {
		val columns = new LinkedList<TableColumn<FXEntity, String>>
		
		if (selectedType == EntityType.ALL) {
			val column = new TableColumn<FXEntity, String>('Entity')
			
			column.cellValueFactory = new EntityCellValueFactory
			columns += column
		} else {	
			for (Property property : selectedType.properties) {
				val column = new TableColumn<FXEntity, String>(property.label)
				
				column.cellValueFactory = new PropertyCellValueFactory(property.index)
				columns += column
			}
		}
		
		update
		entityList.columns.all = columns
	}
	
	override update() {
		val entities = dao.list(selectedType, searchPhrase)
		
		runLater [|
			entityList.items.all = entities.wrap
			listTab.text = '''Results («entities.size»)'''
		]
	}
	
	def openDatabase() {
		// TODO
	}
	
	def showTypeEditor() throws IOException {
		facade.showTypeEditor
	}
}
