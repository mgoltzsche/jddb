package de.algorythm.jdoe.controller

import de.algorythm.jdoe.bundle.Bundle
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.entity.IEntityReference
import de.algorythm.jdoe.model.entity.IPropertyValue
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor
import de.algorythm.jdoe.model.meta.EntityType
import de.algorythm.jdoe.model.meta.propertyTypes.CollectionType
import de.algorythm.jdoe.taskQueue.TaskQueue
import de.algorythm.jdoe.ui.jfx.cell.AssociationContainmentCell
import de.algorythm.jdoe.ui.jfx.controls.EntityField
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.ValueContainer
import de.algorythm.jdoe.ui.jfx.util.IEntityEditorManager
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Collection
import java.util.Date
import java.util.regex.Pattern
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javax.inject.Inject
import org.eclipse.xtext.xbase.lib.Functions.Function1
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0
import org.slf4j.LoggerFactory

import static javafx.application.Platform.*
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue

class PropertyValueEditorVisitor implements IPropertyValueVisitor<FXEntityReference> {

	static val LOG = LoggerFactory.getLogger(typeof(PropertyValueEditorVisitor))
	static val FIELD_ERROR_STYLE_CLASS = 'field-error'
	static val DECIMAL_PATTERN = Pattern.compile('^\\d*$')
	static val REAL_PATTERN = Pattern.compile('^\\d+((\\.|,)\\d+)?$')

	@Inject extension IDAO<FXEntityReference,IFXPropertyValue<?>,FXEntity> dao
	@Inject extension TaskQueue
	@Inject extension IEntityEditorManager editorManager
	@Inject Bundle bundle
	var GridPane gridPane
	var int row
	var Collection<Procedure0> saveCallbacks
	var Collection<Procedure0> updateCallbacks
	var Collection<FXEntity> createdContainedEntities
	
	new(GridPane gridPane, int row, Collection<FXEntity> createdContainedEntities, Collection<Procedure0> saveCallbacks, Collection<Procedure0> updateCallbacks) {
		this.gridPane = gridPane
		this.row = row
		this.saveCallbacks = saveCallbacks
		this.updateCallbacks = updateCallbacks
		this.createdContainedEntities = createdContainedEntities
	}

	override doWithAssociations(IPropertyValue<Collection<FXEntityReference>,FXEntityReference> propertyValue) {
		val property = propertyValue.property
		val collectionType = property.type as CollectionType
		val entityType = collectionType.itemType
		val vBox = new VBox
		val selectedEntities = new ListView<FXEntityReference>
		val addButton = new Button(bundle.add)
		val vBoxChildren = vBox.children
		
		selectedEntities.cellFactory = AssociationContainmentCell.FACTORY
		selectedEntities.items.all = propertyValue.value
		
		if (property.containment) {
			vBoxChildren += selectedEntities
			vBoxChildren += addButton
			
			addButton.setOnAction [
				val newEntity = entityType.createEntity
				
				createdContainedEntities += newEntity
				selectedEntities.items += newEntity
				
				newEntity.showEntityEditor []
			]
			
			updateCallbacks += [| // update selected entities
				val iter = selectedEntities.items.iterator
				
				while (iter.hasNext)
					if (!iter.next.exists)
						iter.remove
			]
		} else {
			val hBox = new HBox
			val hBoxChildren = hBox.children
			val createButton = new Button(bundle.create)
			val addEntityField = new EntityField [searchPhrase,it|
				runReplacedTask('search-associations') [|
					all = entityType.list(searchPhrase)
				]
			]
			val selectionChangeListener = [|
				addButton.disable = addEntityField.value == null ||
					selectedEntities.items.contains(addEntityField.value)
			]
			addButton.disable = true
			addEntityField.valueProperty.addListener [
				selectionChangeListener.apply
			]
			selectedEntities.items.addListener [
				selectionChangeListener.apply
			]
			
			addButton.setOnAction [
				val selectedEntity = addEntityField.value
				
				if (selectedEntity != null) {
					selectedEntities.items += selectedEntity
					addEntityField.value = null
				}
			]
			
			createButton.setOnAction [
				entityType.createEntity.showEntityEditor [
					save
					selectedEntities.items += it
				]
			]
			
			hBoxChildren += addEntityField
			hBoxChildren += addButton
			hBoxChildren += createButton
			vBoxChildren += selectedEntities
			vBoxChildren += hBox
			
			updateCallbacks += [| // remove deleted entities
				val iter = selectedEntities.items.iterator
				
				while (iter.hasNext)
					if (!iter.next.exists)
						iter.remove
				
				addEntityField.update
			]
		}
		
		gridPane.add(vBox, 1, row)
		
		saveCallbacks += [|
			propertyValue.value = selectedEntities.items
		]
	}
	
	override doWithAssociation(IPropertyValue<FXEntityReference,FXEntityReference> propertyValue) {
		val property = propertyValue.property
		val entityType = property.type as EntityType
		val entity = propertyValue.value
		val HBox hBox = new HBox
		val hBoxChildren = hBox.children
		val removeButton = new Button(bundle.remove)
		val editButtonLabel = if (entity == null)
					bundle.create
				else
					bundle.edit
		val editButton = new Button(editButtonLabel)
		
		if (entity == null)
			removeButton.disable = true
		
		if (property.containment) {
			val valueContainer = new ValueContainer<FXEntity>
			val label = new Label
			
			if (entity != null) {
				valueContainer.value = entity
				label.textProperty.bind(valueContainer.value.labelProperty)
			}
			
			hBoxChildren += label
			hBoxChildren += editButton
			hBoxChildren += removeButton
			
			editButton.setOnAction [
				var value = valueContainer.value
				
				if (value == null) {
					value = entityType.createEntity
					valueContainer.value = value
					editButton.text = bundle.edit
					removeButton.disable = false
					createdContainedEntities += value
				}
				
				label.textProperty.bind(value.labelProperty)
				
				value.showEntityEditor []
			]
			
			removeButton.setOnAction [
				val containedEntity = valueContainer.value
				valueContainer.value = null
				label.textProperty.unbind
				label.text = ''
				editButton.text = bundle.create
				containedEntity.closeEntityEditor
				removeButton.disable = true
			]
			
			saveCallbacks += [|
				propertyValue.value = valueContainer.value
			]
			
			updateCallbacks += [|
				val containerValue = valueContainer.value
				
				if (containerValue != null) {
					if (containerValue.update) {
						runLater [|
							containerValue.applyPropertyValues
						]
					} else {
						runLater [|
							label.textProperty.unbind
							label.text = ''
							valueContainer.value = null
							editButton.text = bundle.create
						]
					}
				}
			]
		} else {
			val entityField = new EntityField [searchPhrase,it|
				runReplacedTask('search-association') [|
					all = entityType.list(searchPhrase)
				]
			]
			
			hBoxChildren += entityField
			hBoxChildren += editButton
			hBoxChildren += removeButton
			
			entityField.value = propertyValue.value
			
			entityField.valueProperty.addListener [
				removeButton.disable = entityField.value == null
				editButton.text = if (removeButton.disable)
						bundle.create
					else
						bundle.create
			]
			
			editButton.setOnAction [
				val selectedEntity = entityField.value
				
				if (selectedEntity == null) {
					entityType.createEntity.showEntityEditor [
						save
						entityField.value = it
						removeButton.disable = false
					]
				} else {
					selectedEntity.showEntityEditor
				}
			]
			
			removeButton.setOnAction [
				entityField.value = null
				removeButton.disable = true
			]
			
			saveCallbacks += [|
				propertyValue.value = entityField.value
			]
			
			updateCallbacks += [|
				val selectedEntity = entityField.value
				
				if (selectedEntity != null) {
					if (selectedEntity.update) {
						runLater [|
							selectedEntity.applyPropertyValues
						]
					} else {
						runLater [|
							entityField.value = null
							removeButton.disable = true
						]
					}
				}
				
				entityField.update
			]
		}
		
		gridPane.add(hBox, 1, row)
	}

	override doWithBoolean(IPropertyValue<Boolean,?> propertyValue) {
		val checkBox = new CheckBox
		
		checkBox.selected = propertyValue.value
		
		checkBox.selectedProperty.addListener [
			propertyValue.value = checkBox.selected
		]
		
		gridPane.add(checkBox, 1, row)
		
		saveCallbacks += [|
			propertyValue.value = checkBox.selected
		]
	}

	override doWithDecimal(IPropertyValue<Long,?> propertyValue) {
		val textField = new TextField(propertyValue.toString)
		
		textField.validate [
			empty || DECIMAL_PATTERN.matcher(it).matches
		]
		
		gridPane.add(textField, 1, row)
		
		saveCallbacks += [|
			propertyValue.value = textField.text.asLong
		]
	}
	
	def private asLong(String txt) {
		if (!txt.empty) {
			try {
				return Long.valueOf(txt)
			} catch(NumberFormatException e) {
				LOG.warn('Invalid decimal format: ' + txt)
			}
		}
		
		return null
	}

	override doWithReal(IPropertyValue<Double,?> propertyValue) {
		val textField = new TextField(propertyValue.toString)
		
		gridPane.add(textField, 1, row)
		
		textField.validate [
			if (empty)
				true
			else
				try {
					asNumber
					REAL_PATTERN.matcher(it).matches
				} catch(ParseException e) {
					false
				}
		]
		
		saveCallbacks += [|
			try {
				val number = textField.text.asNumber
				
				propertyValue.value = if (number == null)	
						null
					else
						number.doubleValue
			} catch(ParseException e) {
				propertyValue.value = null
				LOG.warn('Invalid real format: ' + textField.text)
			}
		]
	}
	
	def private asNumber(String dbl) {
		var txt = dbl
		
		return if (txt != null && !txt.empty)
				NumberFormat.instance.parse(txt)
			else
				null
	}

	override doWithDate(IPropertyValue<Date,?> propertyValue) {
		val format = new SimpleDateFormat
		val textField = new TextField(propertyValue.toString)
		
		textField.validate [
			if (empty) {
				true
			} else {
				try {
					format.parse(it)
					true
				} catch(ParseException e) {
					false
				}
			}
		]
		
		gridPane.add(textField, 1, row)
		
		saveCallbacks += [|
			val txt = textField.text
			
			propertyValue.value = if (txt == null || txt.empty)
					null
				else
					try {
						format.parse(txt)
					} catch(ParseException e) {
						LOG.warn('Invalid date format: ' + txt)
						null
					}
		]
	}

	override doWithString(IPropertyValue<String,?> propertyValue) {
		val textField = new TextField(propertyValue.value)
		
		gridPane.add(textField, 1, row)
		
		saveCallbacks += [|
			val txt = textField.text
			
			propertyValue.value = if (txt == null || txt.empty)
					null
				else
					txt
		]
	}

	override doWithText(IPropertyValue<String,?> propertyValue) {
		val textArea = new TextArea(propertyValue.value)
		
		gridPane.add(textArea, 1, row)
		
		saveCallbacks += [|
			val txt = textArea.text
			
			propertyValue.value = if (txt == null || txt.empty)
					null
				else
					txt
		]
	}
	
	def private void validate(TextField field, Function1<String, Boolean> validator) {
		field.textProperty.addListener [c,o,newValue|
			if (!validator.apply(newValue))
				field.styleClass += FIELD_ERROR_STYLE_CLASS
			else
				field.styleClass -= FIELD_ERROR_STYLE_CLASS
		]
	}
}