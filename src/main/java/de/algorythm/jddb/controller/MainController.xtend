package de.algorythm.jddb.controller

import de.algorythm.jddb.JddbFacade
import de.algorythm.jddb.bundle.Bundle
import de.algorythm.jddb.model.dao.IDAO
import de.algorythm.jddb.model.dao.IObserver
import de.algorythm.jddb.model.dao.ModelChange
import de.algorythm.jddb.model.meta.MEntityType
import de.algorythm.jddb.model.meta.MEntityTypeWildcard
import de.algorythm.jddb.taskQueue.ITaskPriority
import de.algorythm.jddb.ui.jfx.controls.FXEntityDetailView
import de.algorythm.jddb.ui.jfx.controls.FXEntityTableView
import de.algorythm.jddb.ui.jfx.model.ApplicationStateModel
import de.algorythm.jddb.ui.jfx.model.FXEntity
import de.algorythm.jddb.ui.jfx.model.FXEntityReference
import de.algorythm.jddb.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jddb.ui.jfx.taskQueue.FXTaskQueue
import de.algorythm.jddb.ui.jfx.util.EntityEditorViewRegistry
import java.io.IOException
import java.net.URL
import java.util.LinkedList
import java.util.ResourceBundle
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.stage.DirectoryChooser
import javax.inject.Inject

import static javafx.application.Platform.*

public class MainController implements Initializable, IObserver<FXEntity, IFXPropertyValue<?>, FXEntityReference> {
	
	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@Inject extension FXTaskQueue
	@Inject extension JddbFacade facade
	@Inject extension EntityEditorViewRegistry
	@Inject var Bundle bundle
	@FXML var ApplicationStateModel appState
	@FXML var Button openDbButton
	@FXML var ComboBox<MEntityType> typeComboBox
	@FXML var TabPane tabs
	@FXML var Tab listTab
	@FXML var FXEntityTableView entityTable
	@FXML var FXEntityDetailView entityDetails
	@FXML var TextField searchField
	@FXML var MenuButton newEntityButton
	var selectedType = MEntityTypeWildcard.instance
	var String searchPhrase
	val ObservableList<MEntityType> entityTypes = FXCollections.observableList(newLinkedList)
	
	override initialize(URL url, ResourceBundle resourceBundle) {
		tabPane = tabs
		
		entityDetails.entityProperty.bind(entityTable.selectionModel.selectedItemProperty)
		
		entityTable.setOnRowClick [
			showEntityEditor
		]
		entityTable.setOnRowEnter [it, node|
			showEntityDetails(node)
		]
		entityTable.items.addListener [
			listTab.text = '''«bundle.results» («entityTable.items.size»)'''
		]
		
		searchField.textProperty.addListener [
			searchPhrase = searchField.text
			showListTab
			search
		]
		
		typeComboBox.valueProperty.addListener [
			setSelectedType(typeComboBox.value)
		]
		
		entityTypes.addListener [
			setupTypeSelection
			setupNewEntityButton
		]
		
		runLater [|
			openDbButton.requestFocus
		]
		
		addObserver(this)
	}
	
	def SimpleStringProperty searchValueProperty() {
		searchValueProperty
	}
	
	def private void setSelectedType(MEntityType type) {
		selectedType = if (type == null)
				MEntityTypeWildcard.instance
			else
				type
		
		val selectedType = selectedType
		val searchPhrase = searchPhrase
		
		runTask('switch-search-type', '''«bundle.taskSwitchSearchType»: «selectedType.label»''', ITaskPriority.FIRST) [|
			runLater [|
				showListTab
			]
			
			val entities = dao.list(selectedType, searchPhrase)
			
			runLater [|
				entityTable.items.all = entities
				entityTable.entityType = selectedType
			]
		]
	}
	
	def private void showListTab() {
		tabs.selectionModel.select(listTab)
	}
	
	def private void setupTypeSelection() {
		val types = new LinkedList<MEntityType>(entityTypes)
		
		types.addFirst(MEntityTypeWildcard.instance)
		
		typeComboBox.items.all = types
		typeComboBox.selectionModel.selectFirst
	}
	
	def private void setupNewEntityButton() {
		val menuItems = new LinkedList<MenuItem>
		
		for (type : entityTypes.filter[t|!t.isEmbedded]) {
			val menuItem = new MenuItem
			
			menuItem.text = type.label
			
			menuItem.setOnAction [
				type.createNewEntity.showEntityEditor
			]
			
			menuItems += menuItem
		}
		
		newEntityButton.items.all = menuItems
	}
	
	override update(ModelChange<FXEntity, IFXPropertyValue<?>, FXEntityReference> change) {
		if (change.newOrDeleted) {
			runLater [|
				search
			]
		}
	}
	
	def private void search() {
		val selectedType = selectedType
		val searchPhrase = searchPhrase
		
		runTask('search', '''«bundle.taskSearch»: «searchPhrase» («selectedType.label»)''', ITaskPriority.FIRST) [|
			val entities = dao.list(selectedType, searchPhrase)
			
			runLater [|
				entityTable.items.all = entities
			]
		]
	}
	
	def showDatabaseCreateDialog() {
		val dc = new DirectoryChooser
		dc.title = bundle.taskOpenDB
		var dbFile = dc.showDialog(openDbButton.scene.window)
		
		if (dbFile != null)
			dbFile.createDB
	}
	
	def showDatabaseOpenDialog() {
		val dc = new DirectoryChooser
		dc.title = bundle.taskOpenDB
		var dbFile = dc.showDialog(openDbButton.scene.window)
		
		if (dbFile != null)		
			dbFile.openDB
	}
	
	def showTypeEditor() throws IOException {
		facade.showTypeEditor
	}
	
	def onReload() {
		entityTable.items.clear
		onDatabaseOpened
	}
	
	def onDatabaseOpened() {
		entityTypes.all = schema.types
		appState.dbClosed = false
		searchField.requestFocus
		search
	}
	
	def onDatabaseClose() {
		appState.dbClosed = true
		entityTable.items.clear
	}
}
