package de.algorythm.jdoe.controller

import de.algorythm.jdoe.JavaDbObjectEditorFacade
import de.algorythm.jdoe.bundle.Bundle
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.dao.IObserver
import de.algorythm.jdoe.model.dao.ModelChange
import de.algorythm.jdoe.ui.jfx.controls.FXEntityDetailView
import de.algorythm.jdoe.ui.jfx.controls.FXEntityTableView
import de.algorythm.jdoe.ui.jfx.model.ApplicationStateModel
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue
import de.algorythm.jdoe.ui.jfx.util.ViewRegistry
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.Collection
import java.util.LinkedList
import java.util.ResourceBundle
import javafx.beans.property.SimpleStringProperty
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
import de.algorythm.jdoe.model.meta.MEntityType
import de.algorythm.jdoe.model.meta.MEntityTypeWildcard
import de.algorythm.jdoe.taskQueue.ITaskPriority

public class MainController implements Initializable, IObserver<FXEntity, IFXPropertyValue<?>, FXEntityReference> {
	
	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@Inject extension FXTaskQueue
	@Inject extension JavaDbObjectEditorFacade facade
	@Inject extension ViewRegistry
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
	var selectedType = MEntityTypeWildcard.INSTANCE
	var String searchPhrase
	var Collection<MEntityType> entityTypes
	
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
				MEntityTypeWildcard.INSTANCE
			else
				type
		
		runTask('switch-search-type', '''«bundle.taskSwitchSearchType»: «selectedType.label»''', ITaskPriority.HIGHER) [|
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
		
		types.addFirst(MEntityTypeWildcard.INSTANCE)
		
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
		runTask('search', '''«bundle.taskSearch»: «searchPhrase» («selectedType.label»)''', ITaskPriority.HIGHER) [|
			val entities = dao.list(selectedType, searchPhrase)
			
			runLater [|
				entityTable.items.all = entities
			]
		]
	}
	
	def showDatabaseOpenDialog() {
		val dc = new DirectoryChooser
		dc.title = bundle.taskOpenDB
		var dbFile = dc.showDialog(openDbButton.scene.window)
		
		if (dbFile != null) {
			if (!dbFile.absolutePath.endsWith('.jdoedb'))
				dbFile = new File(dbFile.absolutePath + '.jdoedb')
			
			dbFile.openDatabase
		}
	}
	
	def private openDatabase(File dbFile) {
		entityTable.items.clear
		
		facade.openDB(dbFile) [
			entityTypes = it
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
