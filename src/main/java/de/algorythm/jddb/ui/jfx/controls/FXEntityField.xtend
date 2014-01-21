package de.algorythm.jddb.ui.jfx.controls

import de.algorythm.jddb.ui.jfx.model.FXEntityReference
import java.util.LinkedList
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Side
import javafx.scene.control.ContextMenu
import javafx.scene.control.CustomMenuItem
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2

import static javafx.application.Platform.*
import javafx.beans.property.ObjectProperty

public class EntityField extends TextField implements ChangeListener<String> {
	
	static val FIELD_ERROR_STYLE_CLASS = 'field-error'
	
	val contextMenu = new ContextMenu
	val value = new SimpleObjectProperty<FXEntityReference>
	val availableValues = FXCollections.synchronizedObservableList(FXCollections.observableList(new LinkedList<FXEntityReference>))
	val selectedValueLabelProperty = new SimpleStringProperty
	var Procedure2<String, ObservableList<FXEntityReference>> searchHandler

	new(Procedure2<String, ObservableList<FXEntityReference>> searchHandler) {
		this.searchHandler = searchHandler
		id = 'EntityField'
		setPrefSize(150, 24)
		minHeight = 24
		maxHeight = 24
		promptText = 'Search ...'
		setOnKeyReleased [
			if (code == KeyCode.DOWN)
				contextMenu.requestFocus
		]
		textProperty.addListener(this)
		selectedValueLabelProperty.addListener [
			textInternal = selectedValueLabelProperty.value
		]
		value.addListener [
			val entityLabelProperty = value.value?.labelProperty
			
			selectedValueLabelProperty.unbind
			
			if (entityLabelProperty == null)
				selectedValueLabelProperty.value = ''
			else
				selectedValueLabelProperty.bind(entityLabelProperty)
		]
		availableValues.addListener [
			runLater [|
				if (availableValues.empty) {
					error = true
					
					contextMenu.hide
				} else if (availableValues.size == 1) {
					setValue(availableValues.get(0))
					
					contextMenu.hide
				} else {
					val menuItems = availableValues.createMenuItems
					
					error = true
					contextMenu.items.all = menuItems
					
					if (!contextMenu.showing)
						contextMenu.show(this, Side.BOTTOM, 10, -5)
					
					contextMenu.requestFocus
				}
			]
		]
	}
	
	def void setSearchHandler(Procedure2<String, ObservableList<FXEntityReference>> searchHandler) {
		this.searchHandler = searchHandler
	}
	
	def getValue() {
		value.value
	}
	
	def void setValue(FXEntityReference value) {
		this.value.value = value
		textInternal = if (value == null)
				''
			else
				value.labelProperty.value
		error = false
	}
	
	def ObjectProperty<FXEntityReference> valueProperty() {
		value
	}
	
	def private setTextInternal(String txt) {
		textProperty.removeListener(this)
		text = txt
		textProperty.addListener(this)
	}
	
	def update() {
		if (contextMenu.showing)
			searchHandler.apply(text, availableValues)
	}
	
	def private createMenuItems(Iterable<FXEntityReference> entities) {
		val menuItems = new LinkedList<MenuItem>
		
		for (entity : entities) {
			val itemLabel = new Label
			val item = new CustomMenuItem(itemLabel, true)
			
			itemLabel.textProperty.bind(entity.labelProperty)
			itemLabel.styleClass += 'item-label'
			item.styleClass += 'search-menu-item'
			menuItems += item
			
			item.setOnAction [
				setValue(entity)
			]
		}
		
		menuItems
	}

	def private void setError(boolean error) {
		if (error)
			styleClass += FIELD_ERROR_STYLE_CLASS
		else
			styleClass -= FIELD_ERROR_STYLE_CLASS
	}

	override changed(ObservableValue<? extends String> valueContainer, String oldValue, String newValue) {
		if (newValue.empty) {
			value.value = null
			error = false
			contextMenu.hide
		} else {
			searchHandler.apply(newValue, availableValues)
		}
	}
}
