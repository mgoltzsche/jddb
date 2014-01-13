package de.algorythm.jdoe.ui.jfx.controls

import javafx.stage.Popup
import de.algorythm.jdoe.ui.jfx.model.FXEntity

class FXEntityDetailPopup extends Popup {
	
	val details = new FXEntityDetailView
	
	new() {
		super()
		autoFix = true
		hideOnEscape = true
		content += details
	}
	
	def getEntityProperty() {
		details.entityProperty
	}
	
	def getEntity() {
		details.entity
	}
	
	def setEntity(FXEntity entity) {
		details.entity = entity
	}
}