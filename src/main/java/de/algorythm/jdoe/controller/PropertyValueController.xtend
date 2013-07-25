package de.algorythm.jdoe.controller

import de.algorythm.jdoe.model.entity.IEntity
import de.algorythm.jdoe.model.entity.IPropertyValue
import de.algorythm.jdoe.model.meta.visitor.IPropertyValueVisitor
import java.util.Collection
import java.util.Date
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0
import javax.inject.Inject
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.meta.EntityType

class PropertyValueController extends AbstractController implements IPropertyValueVisitor {

	extension IDAO dao
	var GridPane gridPane
	var int row
	var Collection<Procedure0> saveCallbacks
	var Collection<Procedure0> updateCallbacks
	
	new(GridPane gridPane, int row, IDAO dao, Collection<Procedure0> saveCallbacks, Collection<Procedure0> updateCallbacks) {
		this.gridPane = gridPane
		this.row = row
		this.saveCallbacks = saveCallbacks
		this.updateCallbacks = updateCallbacks
		this.dao = dao
	}

	override doWithEntityCollection(IPropertyValue propertyValue,
			Collection<IEntity> values) {
		val vBox = new VBox
		val hBox = new HBox
		val selectedEntities = new ListView<IEntity>
		val availableEntities = new ComboBox<IEntity>
		val addButton = new Button("add")
		val vBoxChildren = vBox.children
		val hBoxChildren = hBox.children
		
		addButton.actionListener [|
			val selectedEntity = availableEntities.selectionModel.selectedItem
			
			if (selectedEntity != null)
				selectedEntities.items.add(selectedEntity)
		]
		
		selectedEntities.items.all = values
		
		hBoxChildren.add(availableEntities)
		hBoxChildren.add(addButton)
		vBoxChildren.add(selectedEntities)
		vBoxChildren.add(hBox)
		gridPane.add(vBox, 1, row)
		
		saveCallbacks += [|
			propertyValue.value = selectedEntities.items
		]
		
		updateCallbacks += [|
			val type = propertyValue.property.type as EntityType
			
			availableEntities.items.all = type.list
		]
	}
	
	override doWithEntity(IPropertyValue propertyValue, IEntity value) {
		val entityComboBox = new ComboBox<IEntity>
		
		entityComboBox.setValue(value);
		
		entityComboBox.selectionModel.selectedItemProperty.changeListener [
			propertyValue.value = it
		]
		
		gridPane.add(entityComboBox, 1, row)
		
		saveCallbacks += [|
			propertyValue.value = entityComboBox.selectionModel.selectedItem
		]
		
		updateCallbacks += [|
			val type = propertyValue.property.type as EntityType
			entityComboBox.items.all = type.list
		]
	}

	override doWithBoolean(IPropertyValue propertyValue, boolean value) {
		val checkBox = new CheckBox
		
		checkBox.selected = value
		
		checkBox.selectedProperty.changeListener [
			propertyValue.value = it
		]
		
		gridPane.add(checkBox, 1, row)
		
		saveCallbacks += [|
			propertyValue.value = checkBox.selected
		]
	}

	override doWithDecimal(IPropertyValue propertyValue, Long value) {
		// TODO: check format
		val valueStr = if (value == null)
				null
			else
				value.toString
		val textField = new TextField(valueStr)
		
		gridPane.add(textField, 1, row)
		
		saveCallbacks += [|
			val txt = textField.text
			
			if (txt != null) {
				try {
					propertyValue.value = Long.valueOf(txt)
					return;
				} catch(NumberFormatException e) {}
			}
			
			propertyValue.value = null
		]
	}

	override doWithReal(IPropertyValue propertyValue, Double value) {
		// TODO: check format
		val valueStr = if (value == null)
				null
			else
				value.toString
		val textField = new TextField(valueStr)
		
		gridPane.add(textField, 1, row)
		
		saveCallbacks += [|
			val txt = textField.text
			
			if (txt != null) {
				try {
					propertyValue.value = Double.valueOf(txt)
					return;
				} catch(NumberFormatException e) {}
			}
			
			propertyValue.value = null
		]
	}

	override doWithDate(IPropertyValue propertyValue, Date value) {
		// TODO Auto-generated method stub
		
	}

	override doWithString(IPropertyValue propertyValue, String value) {
		val textField = new TextField(value)
		
		gridPane.add(textField, 1, row)
		
		saveCallbacks += [|
			val txt = textField.text
			
			propertyValue.value = if (txt == null)
					null
				else
					txt
		]
	}

	override doWithText(IPropertyValue propertyValue, String value) {
		val textArea = new TextArea(value)
		
		gridPane.add(textArea, 1, row)
		
		saveCallbacks += [|
			val txt = textArea.text
			
			propertyValue.value = if (txt == null)
					null
				else
					txt
		]
	}
}