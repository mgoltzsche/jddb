package de.algorythm.jdoe.controller

import de.algorythm.jdoe.JavaDbObjectEditorFacade
import de.algorythm.jdoe.bundle.Bundle
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.dao.IObserver
import de.algorythm.jdoe.model.dao.ModelChange
import de.algorythm.jdoe.model.meta.EntityType
import de.algorythm.jdoe.model.meta.EntityTypeWildcard
import de.algorythm.jdoe.model.meta.Property
import de.algorythm.jdoe.model.meta.Schema
import de.algorythm.jdoe.ui.jfx.cell.EntityCellValueFactory
import de.algorythm.jdoe.ui.jfx.cell.EntityRow
import de.algorythm.jdoe.ui.jfx.cell.EntityTypeCellValueFactory
import de.algorythm.jdoe.ui.jfx.cell.PropertyValueCell
import de.algorythm.jdoe.ui.jfx.comparator.StringComparator
import de.algorythm.jdoe.ui.jfx.model.ApplicationStateModel
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue
import de.algorythm.jdoe.ui.jfx.util.ViewRegistry
import java.io.IOException
import java.net.URL
import java.util.LinkedList
import java.util.ResourceBundle
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
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

public class MainController implements Initializable, IObserver<FXEntity, IFXPropertyValue<?>, FXEntityReference> {
	
	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@Inject extension FXTaskQueue
	@Inject extension JavaDbObjectEditorFacade facade
	@Inject extension ViewRegistry
	@Inject var Bundle bundle
	@FXML var ApplicationStateModel appState
	@FXML var Button openDbButton
	@FXML var ComboBox<EntityType> typeComboBox
	@FXML var TabPane tabs
	@FXML var Tab listTab
	@FXML var TableView<FXEntity> entityList
	@FXML var TextField searchField
	@FXML var MenuButton newEntityButton
	var Schema schema
	var selectedType = EntityTypeWildcard.INSTANCE
	var String searchPhrase
	
	override initialize(URL url, ResourceBundle resourceBundle) {
		tabPane = tabs
		
		entityList.items.addListener [
			listTab.text = '''«bundle.results» («entityList.items.size»)'''
		]
		
		searchField.textProperty.addListener [
			searchPhrase = searchField.text
			showListTab
			search
		]
		
		entityList.cache = false
		entityList.setRowFactory(new EntityRow.Factory [
			showEntityEditor
		])
		
		typeComboBox.valueProperty.addListener [
			setSelectedType(typeComboBox.value)
		]
		
		runLater [|
			openDbButton.requestFocus
		]
		
		addObserver(this)
	}
	
	def SimpleStringProperty searchValueProperty() {
		searchValueProperty
	}
	
	def private void setSelectedType(EntityType type) {
		selectedType = if (type == null)
				EntityTypeWildcard.INSTANCE
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
		
		types.addFirst(EntityTypeWildcard.INSTANCE)
		
		typeComboBox.items.all = types
		typeComboBox.selectionModel.selectFirst
	}
	
	def private void setupNewEntityButton() {
		val menuItems = new LinkedList<MenuItem>
		
		for (type : schema.types.filter[t|!t.embedded]) {
			val menuItem = new MenuItem
			
			menuItem.text = type.label
			
			menuItem.setOnAction [
				type.createNewEntity.showEntityEditor
			]
			
			menuItems += menuItem
		}
		
		newEntityButton.items.all = menuItems
	}
	
	def private updateTableColumns() {
		val columns = new LinkedList<TableColumn<FXEntity, ?>>
		
		if (selectedType == EntityTypeWildcard.INSTANCE) {
			val typeColumn = new TableColumn<FXEntity, String>(bundle.type)
			val labelColumn = new TableColumn<FXEntity, String>(bundle.entity)
			
			typeColumn.cellValueFactory = EntityTypeCellValueFactory.INSTANCE
			labelColumn.cellValueFactory = EntityCellValueFactory.INSTANCE
			
			labelColumn.comparator = StringComparator.INSTANCE
			typeColumn.comparator = StringComparator.INSTANCE
			
			columns += typeColumn
			columns += labelColumn
		} else {
			var i = 0
			
			for (Property property : selectedType.properties) {
				columns += createTableColumn(property.label, i)
				i = i + 1
			}
		}
		
		val entities = dao.list(selectedType, searchPhrase)
		
		runLater [|
			entityList.items.all = entities
			entityList.columns.all = columns
		]
	}
	
	def private TableColumn<FXEntity,IFXPropertyValue<?>> createTableColumn(String label, int i) {
		val column = new TableColumn<FXEntity,IFXPropertyValue<?>>(label)
		column.cellFactory = new PropertyValueCell.Factory
		column.cellValueFactory = [
			val ObservableValue<IFXPropertyValue<?>> cell = new ReadOnlyObjectWrapper(value.getValue(i))
			cell
		]
		column
	}
	
	override update(ModelChange<FXEntity, IFXPropertyValue<?>, FXEntityReference> change) {
		if (change.newOrDeleted) {
			runLater [|
				search
			]
		}
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
		entityList.items.clear
		
		facade.openDB [|
			schema = dao.schema
			setupTypeSelection
			setupNewEntityButton
			appState.dbClosed = false
			searchField.requestFocus
		]
	}
	
	def showTypeEditor() throws IOException {
		facade.showTypeEditor
	}
}
