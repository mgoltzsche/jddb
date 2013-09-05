package de.algorythm.jdoe.controller

import de.algorythm.jdoe.JavaDbObjectEditorFacade
import de.algorythm.jdoe.bundle.Bundle
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.entity.IPropertyValue
import de.algorythm.jdoe.model.meta.EntityType
import de.algorythm.jdoe.model.meta.propertyTypes.CollectionType
import de.algorythm.jdoe.ui.jfx.cell.AssociationCell
import de.algorythm.jdoe.ui.jfx.controls.EntityField
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.ValueContainer
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociation
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociations
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValueVisitor
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTransactionTask
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Collection
import java.util.Date
import java.util.Map
import java.util.regex.Pattern
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javax.inject.Inject
import org.eclipse.xtext.xbase.lib.Functions.Function1
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

class PropertyValueEditorVisitor implements IFXPropertyValueVisitor {

	static val FIELD_ERROR_STYLE_CLASS = 'field-error'
	static val DECIMAL_PATTERN = Pattern.compile('^\\d*$')
	static val REAL_PATTERN = Pattern.compile('^\\d+((\\.|,)\\d+)?$')
	static val MIN_FIELD_WIDTH = 150
	static val MIN_FIELD_HEIGHT = 19

	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@Inject extension FXTaskQueue
	@Inject extension JavaDbObjectEditorFacade facade
	@Inject Bundle bundle
	var GridPane gridPane
	var int row
	var Collection<Procedure0> updateCallbacks
	var Collection<FXEntityReference> containedNewEntities
	var FXEntityReference entityRef
	var Map<FXEntityReference, Collection<FXTransactionTask>> saveContainmentTasks
	
	new(GridPane gridPane, int row, FXEntityReference entityRef, Map<FXEntityReference, Collection<FXTransactionTask>> saveContainmentTasks, Collection<FXEntityReference> containedNewEntities, Collection<Procedure0> updateCallbacks) {
		this.gridPane = gridPane
		this.row = row
		this.entityRef = entityRef
		this.updateCallbacks = updateCallbacks
		this.saveContainmentTasks = saveContainmentTasks
		this.containedNewEntities = containedNewEntities
	}

	override doWithAssociations(FXAssociations propertyValue) {
		val property = propertyValue.property
		val collectionType = property.type as CollectionType
		val entityType = collectionType.itemType
		val vBox = new VBox
		val selectedEntities = new ListView<FXEntityReference>
		val addButton = new Button(bundle.add)
		val vBoxChildren = vBox.children
		var Procedure1<FXEntityReference> onRemove
		
		if (property.containment) {
			vBoxChildren += selectedEntities
			vBoxChildren += addButton
			
			onRemove = [
				saveContainmentTasks.remove(it)
				closeEntityEditor
			]
			
			addButton.setOnAction [
				val newEntity = entityType.createNewEntity
				
				newEntity.referringEntities += entityRef
				containedNewEntities += newEntity
				
				newEntity.showEntityEditor [
					saveContainmentTasks.put(newEntity, saveLater)
					selectedEntities.items += newEntity
				]
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
				runTask('''search-«entityRef.id»-«property.name»''', '''«bundle.taskSearch»: «searchPhrase» («entityType.label»)''') [|
					all = entityType.list(searchPhrase)
				]
			]
			HBox.setHgrow(addEntityField, Priority.ALWAYS)
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
				entityType.createNewEntity.showEntityEditor [
					selectedEntities.items += entityReference
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
			
			onRemove = []
		}
		
		selectedEntities.setMinSize(MIN_FIELD_WIDTH, 75)
		selectedEntities.cellFactory = new AssociationCell.Factory(facade, onRemove)
		selectedEntities.itemsProperty.bindBidirectional(propertyValue.valueProperty);
		
		GridPane.setHgrow(vBox, Priority.ALWAYS)
		gridPane.add(vBox, 1, row)
	}
	
	override doWithAssociation(FXAssociation propertyValue) {
		val property = propertyValue.property
		val entityType = property.type as EntityType
		val noRef = propertyValue.value == null
		val HBox hBox = new HBox
		val hBoxChildren = hBox.children
		val removeButton = new Button(bundle.remove)
		val editButtonLabel = if (noRef) {
				removeButton.disable = true
				bundle.create
			} else
				bundle.edit
		val editButton = new Button(editButtonLabel)
		
		propertyValue.valueProperty.addListener [c,o,value|
			val isNull = value == null
			removeButton.disable = isNull
			editButton.text = if (isNull)
					bundle.create
				else
					bundle.edit
		]
		
		if (property.containment) {
			val createdNotAssignedValue = new ValueContainer<FXEntityReference>
			val label = new Label
			
			label.textProperty.bind(propertyValue.labelProperty)
			HBox.setMargin(label, new Insets(3, 10, 0, 0))
			
			hBoxChildren += label
			hBoxChildren += editButton
			hBoxChildren += removeButton
			
			editButton.setOnAction [
				var value = propertyValue.value
				
				if (value == null) {
					value = createdNotAssignedValue.value
					
					if (value == null) {
						val newEntity = entityType.createNewEntity
						
						newEntity.referringEntities += entityRef
						containedNewEntities += newEntity
						
						createdNotAssignedValue.value = newEntity
						value = newEntity
					}
				}
				
				value.showEntityEditor [
					createdNotAssignedValue.value = null
					propertyValue.value = entityReference
					saveContainmentTasks.put(entityReference, saveLater)
				]
			]
			
			removeButton.setOnAction [
				propertyValue.value.closeEntityEditor
				saveContainmentTasks.remove(propertyValue.value)
				propertyValue.value = null
			]
		} else {
			val entityField = new EntityField [searchPhrase,it|
				runTask('''search-«entityRef.id»-«property.name»''', '''«bundle.taskSearch»: «searchPhrase» («entityType.label»)''') [|
					all = entityType.list(searchPhrase)
				]
			]
			HBox.setHgrow(entityField, Priority.ALWAYS)
			
			hBoxChildren += entityField
			hBoxChildren += editButton
			hBoxChildren += removeButton
			
			entityField.valueProperty.bindBidirectional(propertyValue.valueProperty)
			
			editButton.setOnAction [
				val selectedEntity = entityField.value
				
				if (selectedEntity == null) {
					entityType.createNewEntity.showEntityEditor [
						propertyValue.value = entityReference
					]
				} else {
					selectedEntity.showEntityEditor
				}
			]
			
			removeButton.setOnAction [
				propertyValue.value = null
			]
		}
		
		GridPane.setHgrow(hBox, Priority.ALWAYS)
		gridPane.add(hBox, 1, row)
	}

	override doWithBoolean(IPropertyValue<Boolean,?> propertyValue) {
		val checkBox = new CheckBox
		
		checkBox.selected = propertyValue.value
		
		checkBox.selectedProperty.addListener [
			propertyValue.value = checkBox.selected
		]
		
		gridPane.add(checkBox, 1, row)
	}

	override doWithDecimal(IPropertyValue<Long,?> propertyValue) {
		val textField = new TextField(propertyValue.toString)
		
		textField.setMinSize(MIN_FIELD_WIDTH, MIN_FIELD_HEIGHT)
		
		textField.assign [
			var Long value = null
			
			val matchesPattern = DECIMAL_PATTERN.matcher(it).matches
			val valid = empty || matchesPattern
			
			if (!empty && matchesPattern)
				value = Long.valueOf(textField.text)
			
			propertyValue.value = value
			
			valid
		]
		
		GridPane.setHgrow(textField, Priority.ALWAYS)
		gridPane.add(textField, 1, row)
	}
	
	override doWithReal(IPropertyValue<Double,?> propertyValue) {
		val textField = new TextField(propertyValue.toString)
		
		textField.setMinSize(MIN_FIELD_WIDTH, MIN_FIELD_HEIGHT)
		
		textField.assign [
			var Double value = null
			val valid = if (empty)
				true
			else {
				val no = asNumber
				val matchesPattern = REAL_PATTERN.matcher(it).matches
				
				if (matchesPattern && no != null)
					value = no.doubleValue
				
				matchesPattern
			}
			
			propertyValue.value = value
			
			valid
		]
		
		GridPane.setHgrow(textField, Priority.ALWAYS)
		gridPane.add(textField, 1, row)
	}
	
	def private asNumber(String dbl) {
		var txt = dbl
		
		if (txt != null && !txt.empty) {
			try {
				return NumberFormat.instance.parse(txt)
			} catch(ParseException e) {
			}
		}
		
		null
	}

	override doWithDate(IPropertyValue<Date,?> propertyValue) {
		val format = new SimpleDateFormat
		val textField = new TextField(propertyValue.toString)
		
		textField.setMinSize(MIN_FIELD_WIDTH, MIN_FIELD_HEIGHT)
		
		textField.assign [
			var Date value = null
			val valid = if (empty) {
				true
			} else {
				try {
					value = format.parse(it)
					true
				} catch(ParseException e) {
					false
				}
			}
			
			propertyValue.value = value
			
			valid
		]
		
		GridPane.setHgrow(textField, Priority.ALWAYS)
		gridPane.add(textField, 1, row)
	}

	override doWithString(IPropertyValue<String,?> propertyValue) {
		val textField = new TextField
		
		textField.setMinSize(MIN_FIELD_WIDTH, MIN_FIELD_HEIGHT)
		
		propertyValue.bindStringProperty(textField.textProperty)
		GridPane.setHgrow(textField, Priority.ALWAYS)
		gridPane.add(textField, 1, row)
	}

	override doWithText(IPropertyValue<String,?> propertyValue) {
		val textArea = new TextArea
		
		textArea.setMinSize(MIN_FIELD_WIDTH, MIN_FIELD_HEIGHT)
		
		propertyValue.bindStringProperty(textArea.textProperty)
		GridPane.setHgrow(textArea, Priority.ALWAYS)
		gridPane.add(textArea, 1, row)
	}
	
	def private void bindStringProperty(IPropertyValue<String,?> propertyValue, StringProperty textProperty) {
		textProperty.set(propertyValue.value)
		textProperty.addListener [c,o,value|
			propertyValue.value = if (value.empty)
				null
			else
				value
		]
	}
	
	def private void assign(TextField field, Function1<String, Boolean> validator) {
		field.textProperty.addListener [c,o,newValue|
			if (validator.apply(newValue))
				field.styleClass -= FIELD_ERROR_STYLE_CLASS
			else
				field.styleClass += FIELD_ERROR_STYLE_CLASS
		]
	}
}