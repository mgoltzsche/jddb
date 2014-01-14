package de.algorythm.jdoe.controller

import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.meta.Schema
import de.algorythm.jdoe.ui.jfx.cell.PropertyEditCell
import de.algorythm.jdoe.ui.jfx.cell.TypeCell
import de.algorythm.jdoe.ui.jfx.model.meta.FXType
import java.io.IOException
import java.util.ArrayList
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javax.inject.Inject
import org.slf4j.LoggerFactory
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import javafx.fxml.Initializable
import java.net.URL
import java.util.ResourceBundle
import de.algorythm.jdoe.model.meta.MEntityType
import de.algorythm.jdoe.model.meta.MProperty

class TypeEditorController implements Initializable {

	static val log = LoggerFactory.getLogger(typeof(TypeEditorController))
	
	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@FXML var ListView<FXType> typeList
	@FXML var ListView<MProperty> propertyList
	@FXML var Button btnAddProperty
	var ObservableList<FXType> types
	var ObservableList<MProperty> properties
	var MEntityType selectedType
	var Schema schema
	
	override initialize(URL url, ResourceBundle bundle) {
		schema = dao.schema
		
		// setup type list
		types = typeList.items
		
		val fxTypes = new ArrayList<FXType>(schema.types.size)
		
		for (type : schema.types)
			fxTypes += new FXType(type)
		
		types.all = fxTypes
		
		types.addListener [
			val schemaTypes = schema.types
			
			schemaTypes.clear
			
			for (fxType : types)
				schemaTypes += fxType.getBusinessModel
		]
		
		typeList.cellFactory = new TypeCell.Factory
		typeList.editable = true
		typeList.selectionModel.selectedItemProperty.addListener [
			selectType(typeList.selectionModel.selectedItem?.businessModel)
		]
		
		// setup property list
		propertyList.cellFactory = new PropertyEditCell.Factory(types)
		propertyList.editable = true
		properties = propertyList.items
		
		properties.addListener [
			val typeProperties = selectedType.getProperties
			
			typeProperties.clear
			typeProperties += properties
		]
	}
	
	def save() {
		try {
			dao.schema = schema
		} catch (IOException e) {
			log.error('Cannot save schema', e);
		}
	}
	
	def addType() {
		types += new FXType(new MEntityType)
	}
	
	def addProperty() {
		if (selectedType != null)
			properties += new MProperty
	}
	
	def private selectType(MEntityType type) {
		selectedType = type
		
		if (selectedType == null) {
			properties.clear
		} else {
			properties.all = selectedType.getProperties
			
			btnAddProperty.disable = false		
		}
	}
}