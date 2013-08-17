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

class PropertyValueEditorVisitor implements IPropertyValueVisitor {

	static val LOG = LoggerFactory.getLogger(typeof(PropertyValueEditorVisitor))
	static val FIELD_ERROR_STYLE_CLASS = 'field-error'
	static val DECIMAL_PATTERN = Pattern.compile('^\\d*$')
	static val REAL_PATTERN = Pattern.compile('^\\d+((\\.|,)\\d+)?$')

	@Inject extension IDAO dao
	@Inject extension TaskQueue
	@Inject extension IEntityEditorManager editorManager
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

	override doWithAssociations(Associations propertyValue) {
		val property = propertyValue.property
		val collectionType = property.type as CollectionType
		val entityType = collectionType.itemType
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
				val newEntity = new FXEntity(entityType.createEntity)
				
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
			val createButton = new Button('create')
			val addEntityField = new EntityField [searchPhrase,it|
				runReplacedTask('search-associations') [|
					all = entityType.list(searchPhrase).wrap
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
				entityType.createEntity.wrap.showEntityEditor [
					model.save
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
					if (!iter.next.model.update)
						iter.remove
				
				addEntityField.update
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
		val editButtonLabel = if (entity == null)
					'create'
				else
					'edit'
		val editButton = new Button(editButtonLabel)
		
		if (entity == null)
			removeButton.disable = true
		
		if (property.containment) {
			val valueContainer = new ValueContainer<FXEntity>
			val label = new Label
			
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
				
				if (containerValue != null) {
					if (containerValue.model.update) {
						runLater [|
							containerValue.applyPropertyValues
						]
					} else {
						runLater [|
							label.textProperty.unbind
							label.text = ''
							valueContainer.value = null
							editButton.text = 'create'
						]
					}
				}
			]
		} else {
			val entityField = new EntityField [searchPhrase,it|
				runReplacedTask('search-association') [|
					all = entityType.list(searchPhrase).wrap
				]
			]
			
			hBoxChildren += entityField
			hBoxChildren += editButton
			hBoxChildren += removeButton
			
			entityField.value = propertyValue.value.wrap
			
			entityField.valueProperty.addListener [
				removeButton.disable = entityField.value == null
				editButton.text = if (removeButton.disable)
						'create'
					else
						'edit'
			]
			
			editButton.setOnAction [
				val selectedEntity = entityField.value
				
				if (selectedEntity == null) {
					entityType.createEntity.wrap.showEntityEditor [
						model.save
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
				propertyValue.value = entityField.value?.model
			]
			
			updateCallbacks += [|
				val selectedEntity = entityField.value
				
				if (selectedEntity != null) {
					if (selectedEntity.model.update) {
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

	override doWithBoolean(BooleanValue propertyValue) {
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

	override doWithDecimal(DecimalValue propertyValue) {
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

	override doWithReal(RealValue propertyValue) {
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

	override doWithDate(DateValue propertyValue) {
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
						LOG.warn("Invalid date format: " + txt)
						null
					}
		]
	}

	override doWithString(StringValue propertyValue) {
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

	override doWithText(TextValue propertyValue) {
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