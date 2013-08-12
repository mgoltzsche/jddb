package de.algorythm.jdoe.controller

import de.algorythm.jdoe.model.dao.IDAO
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
import de.algorythm.jdoe.ui.jfx.cell.AssociationContainmentCell
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.ValueContainer
import java.util.Collection
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0
import de.algorythm.jdoe.ui.jfx.util.IEntityEditorManager
import javafx.application.Platform

class PropertyValueEditorVisitor extends AbstractXtendController implements IPropertyValueVisitor {

	extension IDAO dao
	extension IEntityEditorManager editorManager
	var GridPane gridPane
	var int row
	var Collection<Procedure0> saveCallbacks
	var Collection<Procedure0> updateCallbacks
	var Collection<FXEntity> createdContainedEntities
	
	new(GridPane gridPane, int row, IDAO dao, IEntityEditorManager editorManager, Collection<FXEntity> createdContainedEntities, Collection<Procedure0> saveCallbacks, Collection<Procedure0> updateCallbacks) {
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
		val selectedEntities = new ListView<FXEntity>
		val addButton = new Button('add')
		val vBoxChildren = vBox.children
		
		selectedEntities.cellFactory = AssociationContainmentCell.FACTORY
		selectedEntities.items.all = propertyValue.value.wrap
		
		if (property.containment) {
			vBoxChildren += selectedEntities
			vBoxChildren += addButton
			
			addButton.setOnAction [
				val newEntity = new FXEntity(collectionType.itemType.createEntity)
				
				selectedEntities.items += newEntity
				createdContainedEntities += newEntity
				
				newEntity.showEntityEditor []
			]
			
			updateCallbacks += [| // update selected entities
				val iter = selectedEntities.items.iterator
				
				while (iter.hasNext)
					if (!iter.next.model.exists)
						iter.remove
			]
		} else {
			val hBox = new HBox
			val hBoxChildren = hBox.children
			val availableEntities = new ComboBox<FXEntity>
			val createButton = new Button('create')
			
			availableEntities.cellFactory = AssociationCell.FACTORY
			
			selectedEntities.items.changeListener [
				while (next) {
					for (entity : removed)
						availableEntities.items += entity
					
					for (entity : addedSubList)
						availableEntities.items -= entity
				}
			]
			
			addButton.setOnAction [
				val selectedEntity = availableEntities.selectionModel.selectedItem
				
				if (selectedEntity != null) {
					selectedEntities.items += selectedEntity
					availableEntities.value = null
				}
			]
			
			createButton.setOnAction [
				collectionType.itemType.createEntity.wrap.showEntityEditor [
					model.save
					selectedEntities.items += it
				]
			]
			
			hBoxChildren += availableEntities
			hBoxChildren += addButton
			hBoxChildren += createButton
			vBoxChildren += selectedEntities
			vBoxChildren += hBox
			
			updateCallbacks += [|
				val entities = collectionType.itemType.list.wrap
				val iter = selectedEntities.items.iterator
				
				while (iter.hasNext)
					if (!iter.next.model.update)
						iter.remove
				
				entities -= selectedEntities.items
				
				Platform.runLater [|
					availableEntities.items.all = entities
				]
			]
		}
		
		gridPane.add(vBox, 1, row)
		
		saveCallbacks += [|
			propertyValue.value = selectedEntities.items.map(e|e.model)
		]
	}
	
	override doWithAssociation(Association propertyValue) {
		val property = propertyValue.property
		val entityType = property.type as EntityType
		val entity = propertyValue.value
		val HBox hBox = new HBox
		val hBoxChildren = hBox.children
		val removeButton = new Button('remove')
		
		if (entity == null)
			removeButton.disable = true
		
		if (property.containment) {
			val valueContainer = new ValueContainer<FXEntity>
			val label = new Label
			val editButtonLabel = if (entity == null)
					'create'
				else
					'edit'
			val editButton = new Button(editButtonLabel)
			
			if (entity != null) {
				valueContainer.value = entity.wrap
				label.textProperty.bind(valueContainer.value.label)
			}
			
			hBoxChildren += label
			hBoxChildren += editButton
			hBoxChildren += removeButton
			
			editButton.setOnAction [
				var value = valueContainer.value
				
				if (value == null) {
					value = new FXEntity(entityType.createEntity)
					valueContainer.value = value
					editButton.text = 'edit'
					removeButton.disable = false
					createdContainedEntities += value
				}
				
				label.textProperty.bind(value.label)
				
				value.showEntityEditor []
			]
			
			removeButton.setOnAction [
				val containedEntity = valueContainer.value
				valueContainer.value = null
				label.textProperty.unbind
				label.text = ''
				editButton.text = 'create'
				containedEntity.closeEntityEditor
				removeButton.disable = true
			]
			
			saveCallbacks += [|
				propertyValue.value = valueContainer.value?.model
			]
			
			updateCallbacks += [|
				val containerValue = valueContainer.value
				
				if (containerValue != null && !containerValue.model.exists) {
					Platform.runLater [|
						label.textProperty.unbind
						label.text = ''
						valueContainer.value = null
						editButton.text = 'create'
					]
				}
			]
		} else {
			val entityComboBox = new ComboBox<FXEntity>
			val createButton = new Button('create')
			
			hBoxChildren += entityComboBox
			hBoxChildren += createButton
			hBoxChildren += removeButton
			
			entityComboBox.value = propertyValue.value.wrap
			
			entityComboBox.selectionModel.selectedItemProperty.changeListener [
				if (it != null)
					removeButton.disable = false
			]
			
			createButton.setOnAction [
				entityType.createEntity.wrap.showEntityEditor [
					model.save
					entityComboBox.value = it
					removeButton.disable = false
				]
			]
			
			removeButton.setOnAction [
				entityComboBox.value = null
				removeButton.disable = true
			]
			
			saveCallbacks += [|
				propertyValue.value = entityComboBox.value.model
			]
			
			updateCallbacks += [|
				val selectedEntity = entityComboBox.value
				
				if (selectedEntity != null && !selectedEntity.model.update) {
					Platform.runLater [|
						entityComboBox.value = null
						removeButton.disable = true
					]
				}
				
				entityComboBox.items.all = entityType.list.wrap
			]
		}
		
		gridPane.add(hBox, 1, row)
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