package de.algorythm.jddb.controller

import de.algorythm.jddb.JavaDesktopDatabaseFacade
import de.algorythm.jddb.model.dao.IDAO
import de.algorythm.jddb.ui.jfx.cell.MetamodelElementCellFactories
import de.algorythm.jddb.ui.jfx.model.FXEntity
import de.algorythm.jddb.ui.jfx.model.FXEntityReference
import de.algorythm.jddb.ui.jfx.model.meta.FXEntityType
import de.algorythm.jddb.ui.jfx.model.meta.FXProperty
import de.algorythm.jddb.ui.jfx.model.meta.transform.FXModelTransformation
import de.algorythm.jddb.ui.jfx.model.meta.transform.ModelTransformation
import de.algorythm.jddb.ui.jfx.model.propertyValue.IFXPropertyValue
import java.net.URL
import java.util.LinkedList
import java.util.ResourceBundle
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ListView
import javax.inject.Inject
import javafx.beans.Observable
import java.util.LinkedHashSet
import java.util.HashSet
import javafx.beans.property.SimpleStringProperty

class TypeEditorController implements Initializable, InvalidationListener {
	
	@Inject extension JavaDesktopDatabaseFacade
	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	extension ModelTransformation = new ModelTransformation(this)
	@Inject extension FXModelTransformation
	@FXML var ListView<FXEntityType> typeList
	@FXML var ListView<FXProperty> propertyList
	val types = new SimpleListProperty<FXEntityType>(FXCollections.observableList(new LinkedList<FXEntityType>))
	val noTypeSelectedProperty = new SimpleBooleanProperty(true)
	val unsavableProperty = new SimpleBooleanProperty(true)
	val errorMessageProperty = new SimpleStringProperty()
	
	override initialize(URL url, ResourceBundle bundle) {
		// setup type list
		types.all = schema.types.transform
		typeList.cellFactory = new MetamodelElementCellFactories.TypeCellFactory
		typeList.editable = true
		typeList.selectionModel.selectedItemProperty.addListener [v,o,n|
			propertyList.items = if (n == null)
				null
			else
				n.propertiesProperty
		]
		typeList.items = types 
		
		// setup property list
		propertyList.cellFactory = new MetamodelElementCellFactories.PropertyCellFactory(types)
		propertyList.editable = true
		propertyList.itemsProperty.addListener [v,o,n|
			noTypeSelectedProperty.value = n == null
		]
	}
	
	def getErrorMessage() {
		errorMessageProperty.value
	}
	
	def setErrorMessage(String value) {
		errorMessageProperty.value = value
	}
	
	def errorMessageProperty() {
		errorMessageProperty
	}
	
	def isUnsavable() {
		unsavableProperty.value
	}
	
	def setUnsavable(Boolean value) {
		unsavableProperty.value = value
	}
	
	def unsavableProperty() {
		unsavableProperty
	}
	
	def isNoTypeSelected() {
		noTypeSelectedProperty.value
	}
	
	def setNoTypeSelected(Boolean value) {
		noTypeSelectedProperty.value = value
	}
	
	def noTypeSelectedProperty() {
		noTypeSelectedProperty
	}
	
	def save() {
		types.transform.updateSchema
	}
	
	def addType() {
		types += new FXEntityType(this)
	}
	
	def addProperty() {
		propertyList.itemsProperty.value += new FXProperty(this)
	}
	
	override invalidated(Observable property) {
		validate
	}
	
	def validate() {
		val errors = new LinkedHashSet<String>
		val typeNames = new HashSet<String>
		
		for (type : types) {
			val typeLabel = type.label
			val propertyNames = new HashSet<String>
			
			if (typeLabel.empty)
				errors += "Empty type label(s)"
			else if (!typeNames.add(typeLabel))
				errors += "Duplicate type label: " + typeLabel
			
			for (property : type.properties) {
				val propertyLabel = property.label
				if (propertyLabel.empty)
					errors += "Empty property label(s) in type: " + typeLabel
				else if (!propertyNames.add(propertyLabel))
					errors += "Duplicate property label " + propertyLabel + " in " + typeLabel
			}
		}
		
		unsavableProperty.value = !errors.empty
		errorMessageProperty.value = errors.join('\n')
	}
}