package de.algorythm.jdoe.controller

import de.algorythm.jdoe.JavaDbObjectEditorFacade
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.ui.jfx.cell.MetamodelElementCellFactories
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.meta.FXEntityType
import de.algorythm.jdoe.ui.jfx.model.meta.FXProperty
import de.algorythm.jdoe.ui.jfx.model.meta.transform.FXModelTransformation
import de.algorythm.jdoe.ui.jfx.model.meta.transform.ModelTransformation
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import java.net.URL
import java.util.LinkedList
import java.util.ResourceBundle
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javax.inject.Inject

class TypeEditorController implements Initializable {
	
	@Inject extension JavaDbObjectEditorFacade
	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@Inject extension ModelTransformation
	@Inject extension FXModelTransformation
	@FXML var ListView<FXEntityType> typeList
	@FXML var ListView<FXProperty> propertyList
	@FXML var Button btnAddProperty
	val types = new SimpleListProperty<FXEntityType>(FXCollections.observableList(new LinkedList<FXEntityType>));
	
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
			btnAddProperty.disable = n == null
		]
	}
	
	def save() {
		types.transform.updateSchema
	}
	
	def addType() {
		types += new FXEntityType
	}
	
	def addProperty() {
		propertyList.itemsProperty.value += new FXProperty
	}
}