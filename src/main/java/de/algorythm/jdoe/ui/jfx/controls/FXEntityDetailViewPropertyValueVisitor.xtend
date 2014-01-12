package de.algorythm.jdoe.ui.jfx.controls

import de.algorythm.jdoe.model.entity.IPropertyValue
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.util.ImageLoader
import java.util.Collection
import java.util.Date
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox

import static extension de.algorythm.jdoe.ui.jfx.util.OpenFileUtil.*

class FXEntityDetailViewPropertyValueVisitor implements IPropertyValueVisitor<FXEntityReference> {
	
	val GridPane gridPane
	val int i
	val ReadOnlyBooleanProperty visibleProperty
	
	new(GridPane gridPane, int i, ReadOnlyBooleanProperty visibleProperty) {
		this.gridPane = gridPane
		this.i = i
		this.visibleProperty = visibleProperty
	}
	
	override doWithAssociation(IPropertyValue<FXEntityReference,FXEntityReference> propertyValue) {
		addPropertyValueLabel(propertyValue)
	}
	
	override doWithAssociations(IPropertyValue<Collection<FXEntityReference>,FXEntityReference> propertyValue) {
		addPropertyValueLabel(propertyValue)
	}
	
	override doWithBoolean(IPropertyValue<Boolean,?> propertyValue) {
		addPropertyValueLabel(propertyValue)
	}
	
	override doWithDate(IPropertyValue<Date,?> propertyValue) {
		addPropertyValueLabel(propertyValue)
	}
	
	override doWithDecimal(IPropertyValue<Long,?> propertyValue) {
		addPropertyValueLabel(propertyValue)
	}
	
	override doWithReal(IPropertyValue<Double,?> propertyValue) {
		addPropertyValueLabel(propertyValue)
	}
	
	override doWithString(IPropertyValue<String,?> propertyValue) {
		addPropertyValueLabel(propertyValue)
	}
	
	override doWithText(IPropertyValue<String,?> propertyValue) {
		addPropertyValueLabel(propertyValue)
	}
	
	override doWithFile(IPropertyValue<String,?> propertyValue) {
		val VBox vBox = new VBox
		val filePath = propertyValue.value
		val valueLabel = new Label(propertyValue.toString())
		
		vBox.children += valueLabel
		
		if (filePath != null) {
			val imageView = new ImageView
			val filePathProperty = new SimpleStringProperty(filePath)
			
			ImageLoader.instance.bindImage(imageView, filePathProperty, visibleProperty)
			
			vBox.children += imageView
			
			if (openFileSupported) {
				imageView.setOnMouseClicked [
					filePath.openFileExternally
				]
			}
		}
		
		gridPane.add(vBox, 1, i)
	}
	
	def private void addPropertyValueLabel(IPropertyValue<?, ?> propertyValue) {
		val valueLabel = new Label(propertyValue.toString)
		GridPane.setValignment(valueLabel, VPos.TOP)
		GridPane.setMargin(valueLabel, new Insets(4, 0, 0, 0))
		
		gridPane.add(valueLabel, 1, i)
	}
}