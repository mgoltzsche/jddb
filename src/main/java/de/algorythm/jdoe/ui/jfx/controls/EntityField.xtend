package de.algorythm.jdoe.ui.jfx.controls

import de.algorythm.jdoe.ui.jfx.model.FXEntity
import java.util.LinkedList
import javafx.animation.KeyFrame
import javafx.animation.Timeline
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
import javafx.scene.control.Tooltip
import javafx.scene.input.KeyCode
import javafx.scene.layout.Region
import javafx.util.Duration
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2

import static javafx.application.Platform.*

public class EntityField extends Region implements ChangeListener<String> {
	
	static val FIELD_ERROR_STYLE_CLASS = 'field-error'
	
	val textField = new TextField
	val contextMenu = new ContextMenu
	val value = new SimpleObjectProperty<FXEntity>
	val availableValues = FXCollections.synchronizedObservableList(FXCollections.observableList(new LinkedList<FXEntity>))
	val selectedValueLabelProperty = new SimpleStringProperty
	var Procedure2<String, ObservableList<FXEntity>> searchHandler
	val searchErrorTooltip = new Tooltip
    var Timeline searchErrorTooltipHidder = null

	new(Procedure2<String, ObservableList<FXEntity>> searchHandler) {
		this.searchHandler = searchHandler
		id = 'EntityField'
		setPrefSize(150, 24)
		minHeight = 24
		maxHeight = 24
		textField.promptText = 'Search ...'
		children += textField
		textField.setOnKeyReleased [
			if (code == KeyCode.DOWN)
				contextMenu.requestFocus
		]
		textField.textProperty.addListener(this)
		selectedValueLabelProperty.addListener [
			text = selectedValueLabelProperty.value
		]
		value.addListener [
			val entityLabelProperty = value.value?.label
			
			selectedValueLabelProperty.unbind
			
			if (entityLabelProperty != null)
				selectedValueLabelProperty.bind(entityLabelProperty)
		]
		availableValues.addListener [
			if (availableValues.empty) {
				runLater [|
					//showError('No match found\ntry sth else')
					error = true
					
					contextMenu.hide
				]
			} else if (availableValues.size == 1) {
				runLater [|
					setValue(availableValues.get(0))
					
					contextMenu.hide
				]
			} else {
				val menuItems = availableValues.createMenuItems
				
				runLater [|
					error = true
					contextMenu.items.all = menuItems
					
					if (!contextMenu.showing)
						contextMenu.show(this, Side.BOTTOM, 10, -5)
					
					contextMenu.requestFocus
				]
			}
		]
	}
	
	def void setSearchHandler(Procedure2<String, ObservableList<FXEntity>> searchHandler) {
		this.searchHandler = searchHandler
	}
	
	def getValue() {
		value.value
	}
	
	def void setValue(FXEntity value) {
		this.value.value = value
		text = if (value == null)
				''
			else
				value.label.value
		error = false
	}
	
	def ObservableValue<FXEntity> valueProperty() {
		value
	}
	
	def private setText(String txt) {
		textField.textProperty.removeListener(this)
		textField.text = txt
		textField.textProperty.addListener(this)
	}
	
	def update() {
		if (contextMenu.showing)
			searchHandler.apply(textField.text, availableValues)
	}
	
	def private createMenuItems(Iterable<FXEntity> entities) {
		val menuItems = new LinkedList<MenuItem>
		
		for (entity : entities) {
			val itemLabel = new Label
			val item = new CustomMenuItem(itemLabel, true)
			
			itemLabel.textProperty.bind(entity.label)
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
			textField.styleClass += FIELD_ERROR_STYLE_CLASS
		else
			textField.styleClass -= FIELD_ERROR_STYLE_CLASS
	}

	def private void showError(String message) {
		searchErrorTooltip.text = message
		if (searchErrorTooltipHidder != null)
			searchErrorTooltipHidder.stop
		if (message != null) {
			val toolTipPos = textField.localToScene(0, textField.layoutBounds.height)
			val x = toolTipPos.x + textField.scene.x + textField.scene.window.x
			val y = toolTipPos.y + textField.scene.y + textField.scene.window.y
			searchErrorTooltip.show(textField.scene.window, x, y)
			searchErrorTooltipHidder = new Timeline
			searchErrorTooltipHidder.keyFrames += new KeyFrame(Duration.seconds(3)) [
				searchErrorTooltip.hide
				searchErrorTooltip.text = null
			]
			searchErrorTooltipHidder.play
		} else {
			searchErrorTooltip.hide
		}
	}

	override layoutChildren() {
		textField.resize(width, height)
	}
	
	override changed(ObservableValue<? extends String> valueContainer, String oldValue, String newValue) {
		val searchPhrase = textField.text
		
		if (searchPhrase.empty) {
			value.value = null
			contextMenu.hide
		} else {
			searchHandler.apply(searchPhrase, availableValues)
		}
	}
}
