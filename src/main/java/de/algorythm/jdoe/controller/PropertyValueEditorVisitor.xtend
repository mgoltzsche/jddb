package de.algorythm.jdoe.controller

import de.algorythm.jdoe.bundle.Bundle
import de.algorythm.jdoe.model.dao.IDAO
import de.algorythm.jdoe.model.entity.IPropertyValue
import de.algorythm.jdoe.model.meta.EntityType
import de.algorythm.jdoe.model.meta.propertyTypes.CollectionType
import de.algorythm.jdoe.ui.jfx.cell.AssociationContainmentCell
import de.algorythm.jdoe.ui.jfx.controls.EntityField
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociation
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociations
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValueVisitor
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue
import de.algorythm.jdoe.ui.jfx.util.IEntityEditorManager
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Collection
import java.util.Date
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
import javafx.scene.layout.VBox
import javax.inject.Inject
import org.eclipse.xtext.xbase.lib.Functions.Function1
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0
import org.slf4j.LoggerFactory

class PropertyValueEditorVisitor implements IFXPropertyValueVisitor {

	static val LOG = LoggerFactory.getLogger(typeof(PropertyValueEditorVisitor))
	static val FIELD_ERROR_STYLE_CLASS = 'field-error'
	static val DECIMAL_PATTERN = Pattern.compile('^\\d*$')
	static val REAL_PATTERN = Pattern.compile('^\\d+((\\.|,)\\d+)?$')

	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@Inject extension FXTaskQueue
	@Inject extension IEntityEditorManager editorManager
	@Inject Bundle bundle
	var GridPane gridPane
	var int row
	var Collection<Procedure0> updateCallbacks
	var Collection<FXEntityReference> createdContainedEntities
	var FXEntity entity
	
	new(GridPane gridPane, int row, FXEntity entity, Collection<FXEntityReference> createdContainedEntities, Collection<Procedure0> updateCallbacks) {
		this.gridPane = gridPane
		this.row = row
		this.entity = entity
		this.updateCallbacks = updateCallbacks
		this.createdContainedEntities = createdContainedEntities
	}

	override doWithAssociations(FXAssociations propertyValue) {
		val property = propertyValue.property
		val collectionType = property.type as CollectionType
		val entityType = collectionType.itemType
		val vBox = new VBox
		val selectedEntities = new ListView<FXEntityReference>
		val addButton = new Button(bundle.add)
		val vBoxChildren = vBox.children
		
		selectedEntities.cellFactory = AssociationContainmentCell.FACTORY
		selectedEntities.itemsProperty.bindBidirectional(propertyValue.valueProperty);
		
		if (property.containment) {
			vBoxChildren += selectedEntities
			vBoxChildren += addButton
			
			addButton.setOnAction [
				val newEntity = entityType.createEntity
				
				createdContainedEntities += newEntity
				selectedEntities.items += newEntity
				
				newEntity.showEntityEditor
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
				runTask('''search-«entity.id»-«property.name»''', '''«bundle.taskSearch»: «searchPhrase» («entityType.label»)''') [|
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
				entityType.createEntity.showEntityEditor [FXEntity it|
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
		
		removeButton.setOnAction [
			if (property.containment)
				propertyValue.value.closeEntityEditor
			
			propertyValue.value = null
		]
		
		propertyValue.valueProperty.addListener [c,o,value|
			val isNull = value == null
			removeButton.disable = isNull
			editButton.text = if (isNull)
					bundle.create
				else
					bundle.edit
		]
		
		if (property.containment) {
			val label = new Label
			
			label.textProperty.bind(propertyValue.labelProperty)
			HBox.setMargin(label, new Insets(3, 10, 0, 0))
			
			hBoxChildren += label
			hBoxChildren += editButton
			hBoxChildren += removeButton
			
			editButton.setOnAction [
				if (propertyValue.value == null) { 
					propertyValue.value = entityType.createEntity
					createdContainedEntities += propertyValue.value
				}
				
				propertyValue.value.showEntityEditor
			]
		} else {
			val entityField = new EntityField [searchPhrase,it|
				runTask('''search-«entity.id»-«property.name»''', '''«bundle.taskSearch»: «searchPhrase» («entityType.label»)''') [|
					all = entityType.list(searchPhrase)
				]
			]
			
			hBoxChildren += entityField
			hBoxChildren += editButton
			hBoxChildren += removeButton
			
			entityField.valueProperty.bindBidirectional(propertyValue.valueProperty)
			
			editButton.setOnAction [
				val selectedEntity = entityField.value
				
				if (selectedEntity == null) {
					entityType.createEntity.showEntityEditor [
						propertyValue.value = it
					]
				} else {
					selectedEntity.showEntityEditor
				}
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
	}

	override doWithDecimal(IPropertyValue<Long,?> propertyValue) {
		val textField = new TextField(propertyValue.toString)
		
		textField.assign [
			var Long value = null
			
			val valid = if (empty || DECIMAL_PATTERN.matcher(it).matches) {
				value = textField.text.asLong
				true
			} else
				false
			
			propertyValue.value = value
			
			valid
		]
		
		gridPane.add(textField, 1, row)
	}
	
	def private asLong(String txt) {
		if (!txt.empty) {
			try {
				return Long.valueOf(txt)
			} catch(NumberFormatException e) {
				LOG.warn('Invalid decimal format: ' + txt)
			}
		}
		
		null
	}

	override doWithReal(IPropertyValue<Double,?> propertyValue) {
		val textField = new TextField(propertyValue.toString)
		
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
		
		gridPane.add(textField, 1, row)
	}
	
	def private asNumber(String dbl) {
		var txt = dbl
		
		if (txt != null && !txt.empty) {
			try {
				return NumberFormat.instance.parse(txt)
			} catch(ParseException e) {
				LOG.warn('Invalid real format: ' + txt)
			}
		}
		
		null
	}

	override doWithDate(IPropertyValue<Date,?> propertyValue) {
		val format = new SimpleDateFormat
		val textField = new TextField(propertyValue.toString)
		
		textField.assign [
			var Date value = null
			val valid = if (empty) {
				true
			} else {
				try {
					value = format.parse(it)
					true
				} catch(ParseException e) {
					LOG.warn('Invalid date format: ' + it)
					false
				}
			}
			
			propertyValue.value = value
			
			valid
		]
		
		gridPane.add(textField, 1, row)
	}

	override doWithString(IPropertyValue<String,?> propertyValue) {
		val textField = new TextField
		
		propertyValue.bindStringProperty(textField.textProperty)
		gridPane.add(textField, 1, row)
	}

	override doWithText(IPropertyValue<String,?> propertyValue) {
		val textArea = new TextArea
		
		propertyValue.bindStringProperty(textArea.textProperty)
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