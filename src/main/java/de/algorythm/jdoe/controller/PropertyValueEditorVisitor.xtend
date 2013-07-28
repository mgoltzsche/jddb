package de.algorythm.jdoe.controller

import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.entity.IEntity
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor
import de.algorythm.jdoe.model.entity.impl.Association
import de.algorythm.jdoe.model.entity.impl.Associations
import de.algorythm.jdoe.model.entity.impl.BooleanValue
import de.algorythm.jdoe.model.entity.impl.DateValue
import de.algorythm.jdoe.model.entity.impl.DecimalValue
import de.algorythm.jdoe.model.entity.impl.RealValue
import de.algorythm.jdoe.model.entity.impl.StringValue
import de.algorythm.jdoe.model.entity.impl.TextValue
import de.algorythm.jdoe.model.meta.EntityType
import de.algorythm.jdoe.model.meta.propertyTypes.CollectionType
import de.algorythm.jdoe.ui.jfx.cell.AssociationCell
import java.util.Collection
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

class PropertyValueEditorVisitor extends AbstractXtendController implements IPropertyValueVisitor {

	extension IDAO dao
	extension IEntityEditorManager editorManager
	var GridPane gridPane
	var int row
	var Collection<Procedure0> saveCallbacks
	var Collection<Procedure0> updateCallbacks
	var Collection<IEntity> createdContainedEntities
	
	new(GridPane gridPane, int row, IDAO dao, IEntityEditorManager editorManager, Collection<IEntity> createdContainedEntities, Collection<Procedure0> saveCallbacks, Collection<Procedure0> updateCallbacks) {
		this.gridPane = gridPane
		this.row = row
		this.saveCallbacks = saveCallbacks
		this.updateCallbacks = updateCallbacks
		this.dao = dao
		this.editorManager = editorManager
		this.createdContainedEntities = createdContainedEntities
	}

	override doWithAssociations(Associations propertyValue) {
		val property = propertyValue.property
		val collectionType = property.type as CollectionType
		val vBox = new VBox
		val selectedEntities = new ListView<IEntity>
		val addButton = new Button("add")
		val vBoxChildren = vBox.children
		
		selectedEntities.cellFactory = AssociationCell.FACTORY
		selectedEntities.items.all = propertyValue.value
		
		if (property.containment) {
			vBoxChildren += selectedEntities
			vBoxChildren += addButton
			
			addButton.actionListener [|
				val newEntity = collectionType.itemType.createEntity
				
				createdContainedEntities += newEntity
				
				newEntity.showEntityEditor [
					if (id == null) {
						if (!selectedEntities.items.contains(it))
							selectedEntities.items += it
					} else
						save
				]
			]
		} else {
			val hBox = new HBox
			val hBoxChildren = hBox.children
			val availableEntities = new ComboBox<IEntity>
			val createButton = new Button('create')
			
			selectedEntities.items.changeListener [
				while (next) {
					for (entity : removed)
						availableEntities.items += entity
					
					for (entity : addedSubList)
						availableEntities.items -= entity
				}
			]
			
			addButton.actionListener [|
				val selectedEntity = availableEntities.selectionModel.selectedItem
				
				if (selectedEntity != null) {
					selectedEntities.items += selectedEntity
					availableEntities.value = null
				}
			]
			
			createButton.actionListener[|
				collectionType.itemType.createEntity.showEntityEditor [
					save
					selectedEntities.items += it
				]
			]
			
			hBoxChildren += availableEntities
			hBoxChildren += addButton
			hBoxChildren += createButton
			vBoxChildren += selectedEntities
			vBoxChildren += hBox
			
			updateCallbacks += [|
				val entities = collectionType.itemType.list
				
				entities -= selectedEntities.items
				
				availableEntities.items.all = entities
			]
		}
		
		gridPane.add(vBox, 1, row)
		
		saveCallbacks += [|
			propertyValue.value = selectedEntities.items
		]
	}
	
	override doWithAssociation(Association propertyValue) {
		val entityType = propertyValue.property.type as EntityType
		val entityComboBox = new ComboBox<IEntity>
		val HBox hBox = new HBox
		val hBoxChildren = hBox.children
		val createButton = new Button('create')
		val removeButton = new Button('remove')
		
		hBoxChildren += entityComboBox
		hBoxChildren += createButton
		hBoxChildren += removeButton
		
		entityComboBox.value = propertyValue.value
		
		entityComboBox.selectionModel.selectedItemProperty.changeListener [
			propertyValue.value = it
		]
		
		createButton.actionListener[|
			entityType.createEntity.showEntityEditor [
				save
				entityComboBox.value = it
			]
		]
		
		removeButton.actionListener[|
			entityComboBox.value = null
		]
		
		gridPane.add(hBox, 1, row)
		
		saveCallbacks += [|
			propertyValue.value = entityComboBox.selectionModel.selectedItem
		]
		
		updateCallbacks += [|
			val type = propertyValue.property.type as EntityType
			entityComboBox.items.all = type.list
		]
	}

	override doWithBoolean(BooleanValue propertyValue) {
		val checkBox = new CheckBox
		
		checkBox.selected = propertyValue.value
		
		checkBox.selectedProperty.changeListener [
			propertyValue.value = it
		]
		
		gridPane.add(checkBox, 1, row)
		
		saveCallbacks += [|
			propertyValue.value = checkBox.selected
		]
	}

	override doWithDecimal(DecimalValue propertyValue) {
		// TODO: check format
		val value = propertyValue.value
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

	override doWithReal(RealValue propertyValue) {
		// TODO: check format
		val value = propertyValue.value
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

	override doWithDate(DateValue propertyValue) {
		// TODO Auto-generated method stub
		
	}

	override doWithString(StringValue propertyValue) {
		val textField = new TextField(propertyValue.value)
		
		gridPane.add(textField, 1, row)
		
		saveCallbacks += [|
			val txt = textField.text
			
			propertyValue.value = if (txt == null)
					null
				else
					txt
		]
	}

	override doWithText(TextValue propertyValue) {
		val textArea = new TextArea(propertyValue.value)
		
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