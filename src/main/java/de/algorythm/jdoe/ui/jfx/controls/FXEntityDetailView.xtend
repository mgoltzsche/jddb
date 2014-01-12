package de.algorythm.jdoe.ui.jfx.controls

import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.layout.GridPane

class FXEntityDetailView extends GridPane {

	val entityProperty = new SimpleObjectProperty<FXEntity>
	val gridPane = new GridPane

	new() {
		super()
		children += gridPane
		entityProperty.addListener [
			update
		]
	}
	
	def getEntityProperty() {
		entityProperty
	}
	
	def getEntity() {
		entityProperty.value
	}
	
	def setEntity(FXEntity entity) {
		entityProperty.value = entity
	}
	
	def protected update() {
		val entity = entityProperty.value
		
		gridPane.children.clear
		
		if (entity != null) {
			var i = 0
			
			for (IFXPropertyValue<?> value : entity.values) {
				val label = new Label(value.property.label + ': ')
				
				GridPane.setValignment(label, VPos.TOP)
				GridPane.setMargin(label, new Insets(4, 0, 0, 0))
				
				gridPane.add(label, 0, i)
				
				value.visit(new FXEntityDetailViewPropertyValueVisitor(gridPane, i, visibleProperty))
				
				i = i + 1
			}
		}
	}
}
