package de.algorythm.jddb.controller

import de.algorythm.jddb.JddbFacade
import de.algorythm.jddb.bundle.Bundle
import de.algorythm.jddb.model.dao.IDAO
import de.algorythm.jddb.model.meta.MEntityType
import de.algorythm.jddb.taskQueue.ITaskPriority
import de.algorythm.jddb.ui.jfx.cell.AssociationCell
import de.algorythm.jddb.ui.jfx.controls.EntityField
import de.algorythm.jddb.ui.jfx.loader.image.ImageLoader
import de.algorythm.jddb.ui.jfx.model.FXEntity
import de.algorythm.jddb.ui.jfx.model.FXEntityReference
import de.algorythm.jddb.ui.jfx.model.ValueContainer
import de.algorythm.jddb.ui.jfx.model.propertyValue.AbstractFXAttributeValue
import de.algorythm.jddb.ui.jfx.model.propertyValue.BooleanFXAttributeValue
import de.algorythm.jddb.ui.jfx.model.propertyValue.DateFXAttributeValue
import de.algorythm.jddb.ui.jfx.model.propertyValue.DecimalFXAttributeValue
import de.algorythm.jddb.ui.jfx.model.propertyValue.FXAssociation
import de.algorythm.jddb.ui.jfx.model.propertyValue.FXAssociations
import de.algorythm.jddb.ui.jfx.model.propertyValue.FileFXAttributeValue
import de.algorythm.jddb.ui.jfx.model.propertyValue.IFXPropertyValue
import de.algorythm.jddb.ui.jfx.model.propertyValue.IFXPropertyValueVisitor
import de.algorythm.jddb.ui.jfx.model.propertyValue.RealFXAttributeValue
import de.algorythm.jddb.ui.jfx.model.propertyValue.StringFXAttributeValue
import de.algorythm.jddb.ui.jfx.model.propertyValue.TextFXAttributeValue
import de.algorythm.jddb.ui.jfx.taskQueue.FXTaskQueue
import de.algorythm.jddb.ui.jfx.taskQueue.FXTransactionTask
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Collection
import java.util.Date
import java.util.Map
import java.util.regex.Pattern
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javax.inject.Inject
import org.eclipse.xtext.xbase.lib.Functions.Function1
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

import static extension de.algorythm.jddb.ui.jfx.util.OpenFileUtil.*
import de.algorythm.jddb.model.meta.propertyTypes.CollectionType
import de.algorythm.jddb.bundle.ImageBundle
import javafx.scene.control.Tooltip
import de.algorythm.jddb.model.dao.util.IFilePathConverter
import java.io.File

class PropertyValueEditorVisitor implements IFXPropertyValueVisitor {

	static val EMPTY = ''
	static val FIELD_ERROR_STYLE_CLASS = 'field-error'
	static val DECIMAL_PATTERN = Pattern.compile('^\\d*$')
	static val REAL_PATTERN = Pattern.compile('^\\d+((\\.|,)\\d+)?$')
	static val MIN_FIELD_WIDTH = 150
	static val MIN_FIELD_HEIGHT = 19

	@Inject extension IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	@Inject extension FXTaskQueue
	@Inject extension JddbFacade facade
	@Inject extension IFilePathConverter
	@Inject Bundle bundle
	var GridPane gridPane
	var int row
	var Collection<Procedure0> updateCallbacks
	var Collection<FXEntityReference> containedNewEntities
	var FXEntityReference entityRef
	var Map<FXEntityReference, Collection<FXTransactionTask>> saveContainmentTasks
	val ReadOnlyBooleanProperty visibleProperty
	
	new(GridPane gridPane, int row, FXEntityReference entityRef, Map<FXEntityReference, Collection<FXTransactionTask>> saveContainmentTasks, Collection<FXEntityReference> containedNewEntities, Collection<Procedure0> updateCallbacks, ReadOnlyBooleanProperty visibleProperty) {
		this.gridPane = gridPane
		this.row = row
		this.entityRef = entityRef
		this.updateCallbacks = updateCallbacks
		this.saveContainmentTasks = saveContainmentTasks
		this.containedNewEntities = containedNewEntities
		this.visibleProperty = visibleProperty
	}

	override doWithAssociations(FXAssociations propertyValue) {
		val property = propertyValue.property
		val collectionType = property.getType as CollectionType
		val entityType = collectionType.getItemType
		val vBox = new VBox
		val selectedEntities = new ListView<FXEntityReference>
		val vBoxChildren = vBox.children
		var Procedure1<FXEntityReference> onRemove
		val addButton = new Button => [
			graphic = new ImageView(ImageBundle.instance.add)
			tooltip = new Tooltip(bundle.add)
			disable = !property.containment
		]
		
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
					
					if (!selectedEntities.items.contains(newEntity)) {
						selectedEntities.items += newEntity
					}
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
			val addEntityField = new EntityField [searchPhrase,it|
				runTask('''search-«entityRef.id»-«property.name»''', '''«bundle.taskSearch»: «searchPhrase» («entityType.label»)''', ITaskPriority.FIRST) [|
					all = entityType.list(searchPhrase)
				]
			]
			
			val selectionChangeListener = [|
				addButton.disable = addEntityField.value == null ||
					selectedEntities.items.contains(addEntityField.value)
			]
			
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
			
			HBox.setHgrow(addEntityField, Priority.ALWAYS)
			
			hBoxChildren += addEntityField
			hBoxChildren += addButton
			hBoxChildren += new Button => [
				graphic = new ImageView(ImageBundle.instance.create)
				tooltip = new Tooltip(bundle.create)
				setOnAction [
					entityType.createNewEntity.showEntityEditor [
						selectedEntities.items += entityReference
					]
				]
			]
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
		val entityType = property.getType as MEntityType
		val isNull = propertyValue.value == null
		val HBox hBox = new HBox
		val hBoxChildren = hBox.children
		val removeButton = new Button => [
			graphic = new ImageView(ImageBundle.instance.delete)
			tooltip = new Tooltip(bundle.remove)
		]
		val editButton = new Button
		val buttonUpdater = [boolean isNullValue|
			removeButton.disable = isNullValue
			
			if (isNullValue) {
				editButton.graphic = new ImageView(ImageBundle.instance.create)
				editButton.tooltip = new Tooltip(bundle.create)
			} else {
				editButton.graphic = new ImageView(ImageBundle.instance.edit)
				editButton.tooltip = new Tooltip(bundle.edit)
			}
		]
		
		buttonUpdater.apply(isNull)
		
		propertyValue.valueProperty.addListener [c,o,value|
			buttonUpdater.apply(value == null)
		]
		
		if (property.isContainment) {
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
				runTask('''search-«entityRef.id»-«property.name»''', '''«bundle.taskSearch»: «searchPhrase» («entityType.label»)''', ITaskPriority.FIRST) [|
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

	override doWithBoolean(BooleanFXAttributeValue propertyValue) {
		val checkBox = new CheckBox
		
		checkBox.selected = propertyValue.value
		
		checkBox.selectedProperty.addListener [
			propertyValue.value = checkBox.selected
		]
		
		gridPane.add(checkBox, 1, row)
	}

	override doWithDecimal(DecimalFXAttributeValue propertyValue) {
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
	
	override doWithReal(RealFXAttributeValue propertyValue) {
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

	override doWithDate(DateFXAttributeValue propertyValue) {
		val format = new SimpleDateFormat()
		val textField = new TextField(propertyValue.toString)
		
		textField.promptText = format.toLocalizedPattern
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

	override doWithString(StringFXAttributeValue propertyValue) {
		val textField = new TextField
		
		textField.setMinSize(MIN_FIELD_WIDTH, MIN_FIELD_HEIGHT)
		
		propertyValue.bindStringProperty(textField.textProperty)
		GridPane.setHgrow(textField, Priority.ALWAYS)
		gridPane.add(textField, 1, row)
	}

	override doWithText(TextFXAttributeValue propertyValue) {
		val textArea = new TextArea
		
		textArea.setMinSize(MIN_FIELD_WIDTH, MIN_FIELD_HEIGHT)
		
		propertyValue.bindStringProperty(textArea.textProperty)
		GridPane.setHgrow(textArea, Priority.ALWAYS)
		gridPane.add(textArea, 1, row)
	}
	
	override doWithFile(FileFXAttributeValue propertyValue) {
		val VBox vBox = new VBox
		val HBox hBox = new HBox
		val vBoxChildren = vBox.children
		val hBoxChildren = hBox.children
		val imageView = new ImageView
		val fileField = new TextField
		val fileFieldEmptyBinding = fileField.textProperty.notNull.and(fileField.textProperty.isEqualTo(EMPTY))
		val openButtonSupported = openFileSupported
		
		fileField.editable = false
		fileField.setMinSize(MIN_FIELD_WIDTH, MIN_FIELD_HEIGHT)
		HBox.setHgrow(fileField, Priority.ALWAYS)
		
		hBoxChildren += fileField
		hBoxChildren += new Button => [
			graphic = new ImageView(ImageBundle.instance.chooseFile)
			tooltip = new Tooltip(bundle.chooseFile)
			setOnAction [
				fileField.chooseFile
			]
		]
		if (openButtonSupported) {
			hBoxChildren += new Button => [
				graphic = new ImageView(ImageBundle.instance.details)
				tooltip = new Tooltip(bundle.open)
				disable = true
				disableProperty.bind(fileFieldEmptyBinding)
				setOnAction [
					fileField.text.openFileExternally
				]
			]
			imageView.setOnMouseClicked [
				fileField.text.openFileExternally
			]
		}
		
		hBoxChildren += new Button => [
			graphic = new ImageView(ImageBundle.instance.delete)
			tooltip = new Tooltip(bundle.remove)
			disable = true
			disableProperty.bind(fileFieldEmptyBinding)
			setOnAction [
				fileField.text = ''
			]
		]
		vBoxChildren += hBox
		vBoxChildren += imageView
		
		propertyValue.bindStringProperty(fileField.textProperty)
		ImageLoader.INSTANCE.bindImage(imageView, fileField.textProperty, visibleProperty)
		
		GridPane.setHgrow(vBox, Priority.ALWAYS)
		gridPane.add(vBox, 1, row)
	}
	
	def private chooseFile(TextField fileField) {
		val currentFile = fileField.text
		val fc = new FileChooser
		fc.title = 'Choose file'
		
		if (currentFile == null || currentFile.empty)
			fc.initialDirectory = primaryRootDirectory
		else {
			val currentParentFile = new File(currentFile.toAbsolutePath).parentFile
			
			fc.initialDirectory = if (currentParentFile == null)
				primaryRootDirectory
			else
				currentParentFile
		}
		
		var file = fc.showOpenDialog(gridPane.scene.window)
		
		if (file != null)
			fileField.text = file.toAbstractRelativePath
	}
	
	def private void bindStringProperty(AbstractFXAttributeValue<String> propertyValue, StringProperty textProperty) {
		textProperty.value = propertyValue.value
		textProperty.addListener [c,o,value|
			propertyValue.value = if (value == null || value.empty)
				null
			else
				value
		]
	}
	
	def private void assign(TextField field, Function1<String, Boolean> validator) {
		field.textProperty.addListener [c,o,newValue|
			if (validator.apply(newValue)) {
				field.styleClass -= FIELD_ERROR_STYLE_CLASS
			} else {
				field.styleClass += FIELD_ERROR_STYLE_CLASS
			}
		]
	}
}