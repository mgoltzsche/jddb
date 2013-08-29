package de.algorythm.jdoe.controller

import de.algorythm.jdoe.JavaDbObjectEditorFacade
import de.algorythm.jdoe.bundle.Bundle
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.dao.IObserver
import de.algorythm.jdoe.model.dao.ModelChange
import de.algorythm.jdoe.model.meta.EntityType
import de.algorythm.jdoe.model.meta.Property
import de.algorythm.jdoe.model.meta.Schema
import de.algorythm.jdoe.ui.jfx.cell.EntityCellValueFactory
import de.algorythm.jdoe.ui.jfx.cell.EntityRow
import de.algorythm.jdoe.ui.jfx.cell.EntityTypeCellValueFactory
import de.algorythm.jdoe.ui.jfx.cell.PropertyCellValueFactory
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue
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

import static javafx.application.Platform.*
import javafx.fxml.Initializable
import java.util.ResourceBundle
import java.net.URL

public class MainController implements Initializable, IObserver<FXEntity, IFXPropertyValue<?>, FXEntityReference> {
	
	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@Inject extension FXTaskQueue
	@Inject extension JavaDbObjectEditorFacade facade
	@Inject extension ViewRegistry
	@Inject var Bundle bundle
	@FXML var ComboBox<EntityType> typeComboBox
	@FXML var TabPane tabs
	@FXML var Tab listTab
	@FXML var TableView<FXEntity> entityList
	@FXML var TextField searchField
	@FXML var MenuButton newEntityButton
	var Schema schema
	var selectedType = EntityType.ALL
	var String searchPhrase
	
	override initialize(URL url, ResourceBundle resourceBundle) {
		tabPane = tabs
		
		schema = dao.schema
		
		entityList.items.addListener [
			listTab.text = '''«bundle.results» («entityList.items.size»)'''
		]
		
		searchField.textProperty.addListener [
			searchPhrase = searchField.text
			showListTab
			search
		]
		
		entityList.setRowFactory(new EntityRow.Factory [
			showEntityEditor
		])
		
		typeComboBox.valueProperty.addListener [
			setSelectedType(typeComboBox.value)
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
		
		runTask('switch-search-type', '''«bundle.taskSwitchSearchType»: «selectedType.label»''') [|
			updateTableColumns
			
			runLater [|
				showListTab
			]
		]
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
		
		for (type : schema.types.filter[t|!t.embedded]) {
			val menuItem = new MenuItem
			
			menuItem.text = type.label
			
			menuItem.setOnAction [
				type.createEntity.showEntityEditor
			]
			
			menuItems += menuItem
		}
		
		newEntityButton.items.all = menuItems
	}
	
	def private updateTableColumns() {
		val columns = new LinkedList<TableColumn<FXEntity, String>>
		
		if (selectedType == EntityType.ALL) {
			val typeColumn = new TableColumn<FXEntity, String>(bundle.type)
			val labelColumn = new TableColumn<FXEntity, String>(bundle.entity)
			
			typeColumn.cellValueFactory = EntityTypeCellValueFactory.INSTANCE
			labelColumn.cellValueFactory = EntityCellValueFactory.INSTANCE
			
			columns += typeColumn
			columns += labelColumn
		} else {
			var i = 0
			
			for (Property property : selectedType.properties) {
				val column = new TableColumn<FXEntity, String>(property.label)
				
				column.cellValueFactory = new PropertyCellValueFactory(i)
				columns += column
				i = i + 1
			}
		}
		
		val entities = dao.list(selectedType, searchPhrase)
		
		runLater [|
			entityList.items.all = entities
			entityList.columns.all = columns
		]
	}
	
	override update(ModelChange<FXEntity, IFXPropertyValue<?>, FXEntityReference> change) {
		runLater [|
			val iter = entityList.items.iterator
			var i = 0
			
			while(iter.hasNext) {
				val item = iter.next
				
				if (change.deleted.contains(item))
					iter.remove
				else {
					val savedEntity = change.saved.get(item.id)
					
					if (savedEntity != null) {
						item.assign(savedEntity)
						i = i + 1
					}
				}
			}
			
			if (i < change.saved.size)
				search
		]
	}
	
	def private void search() {
		runTask('search', '''«bundle.taskSearch»: «searchPhrase» («selectedType.label»)''') [|
			val entities = dao.list(selectedType, searchPhrase)
			
			runLater [|
				entityList.items.all = entities
			]
		]
	}
	
	def openDatabase() {
		// TODO
	}
	
	def showTypeEditor() throws IOException {
		facade.showTypeEditor
	}
}
