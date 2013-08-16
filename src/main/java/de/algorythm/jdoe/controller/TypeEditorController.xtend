package de.algorythm.jdoe.controller

import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.meta.EntityType
import de.algorythm.jdoe.model.meta.Property
import de.algorythm.jdoe.model.meta.Schema
import de.algorythm.jdoe.ui.jfx.cell.PropertyEditCell
import de.algorythm.jdoe.ui.jfx.cell.TypeCell
import java.io.IOException
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javax.inject.Inject
import org.slf4j.LoggerFactory
import java.util.ArrayList
import de.algorythm.jdoe.ui.jfx.model.FXType

class TypeEditorController implements IController {

	static val log = LoggerFactory.getLogger(typeof(TypeEditorController))
	
	@Inject extension IDAO dao
	@FXML var ListView<FXType> typeList
	@FXML var ListView<Property> propertyList
	@FXML var Button btnAddProperty
	var ObservableList<FXType> types
	var ObservableList<Property> properties
	var EntityType selectedType
	var Schema schema
	
	override init() {
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
			val typeProperties = selectedType.properties
			
			typeProperties.clear
			typeProperties += properties
		]
	}
	
	def save() {
		try {
			dao.schema = schema
		} catch (IOException e) {
			log.error("Cannot save schema", e);
		}
	}
	
	def addType() {
		types += new FXType(new EntityType)
	}
	
	def addProperty() {
		if (selectedType != null)
			properties += new Property
	}
	
	def private selectType(EntityType type) {
		selectedType = type
		
		if (selectedType == null) {
			properties.clear
		} else {
			properties.all = selectedType.properties
			
			btnAddProperty.disable = false		
		}
	}
}