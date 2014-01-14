package de.algorythm.jdoe.controller

import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.meta.Schema
import de.algorythm.jdoe.ui.jfx.cell.PropertyEditCell
import de.algorythm.jdoe.ui.jfx.cell.TypeCell
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.meta.FXEntityType
import de.algorythm.jdoe.ui.jfx.model.meta.FXProperty
import de.algorythm.jdoe.ui.jfx.model.meta.transform.FXModelTransformation
import de.algorythm.jdoe.ui.jfx.model.meta.transform.ModelTransformation
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import java.io.IOException
import java.net.URL
import java.util.ResourceBundle
import javafx.beans.property.SimpleListProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javax.inject.Inject
import org.slf4j.LoggerFactory
import javafx.collections.FXCollections
import java.util.LinkedList

class TypeEditorController implements Initializable {

	static val log = LoggerFactory.getLogger(typeof(TypeEditorController))
	
	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@FXML var ListView<FXEntityType> typeList
	@FXML var ListView<FXProperty> propertyList
	@FXML var Button btnAddProperty
	var Schema schema
	val types = new SimpleListProperty<FXEntityType>(FXCollections.observableList(new LinkedList<FXEntityType>));
	extension ModelTransformation = new ModelTransformation
	extension FXModelTransformation = new FXModelTransformation
	
	override initialize(URL url, ResourceBundle bundle) {
		schema = dao.schema
		
		// setup type list
		types.all = transform(schema.types)
		typeList.cellFactory = new TypeCell.Factory
		typeList.editable = true
		typeList.selectionModel.selectedItemProperty.addListener [v,o,n|
			propertyList.itemsProperty.value = if (n == null)
				null
			else
				n.propertiesProperty
		]
		typeList.items = types 
		
		// setup property list
		propertyList.cellFactory = new PropertyEditCell.Factory(types)
		propertyList.editable = true
		propertyList.itemsProperty.addListener [v,o,n|
			btnAddProperty.disable = n == null
		]
	}
	
	def save() {
		try {
			transform(types)
			dao.schema = schema
		} catch (IOException e) {
			log.error('Cannot save schema', e);
		}
	}
	
	def addType() {
		types += new FXEntityType
	}
	
	def addProperty() {
		propertyList.itemsProperty.value += new FXProperty
	}
}