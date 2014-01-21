package de.algorythm.jddb.ui.jfx.cell

import de.algorythm.jddb.ui.jfx.model.meta.FXEntityType
import javafx.scene.control.CheckBox
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.Priority
import javafx.geometry.Pos
import javafx.beans.value.ChangeListener

class TypeCell extends AbstractLabeledListCell<FXEntityType> {
	
	val hBox = new HBox
	val spacer = new Region
	val embedded = new CheckBox(bundle.embedded)
	val ChangeListener<Boolean> embeddedChangeListener = [c,o,n|
		editItem.embedded = n
	]
	
	new() {
		super()
		
		spacer.minWidth = 5
		hBox.alignment = Pos.BASELINE_CENTER
		
		val hBoxChildren = hBox.children
		
		hBoxChildren += embedded
		hBoxChildren += spacer
		hBoxChildren += deleteButton
		
		HBox.setHgrow(spacer, Priority.ALWAYS)
		
		editor.children.all = newLinkedList(labelInput, hBox)
	}
	
	override doWithProperties(AbstractLabeledListCell.BindingHandler<FXEntityType> it) {
		super.doWithProperties(it)
		
		doWithProperty(embedded.selectedProperty, editItem.embedded, embeddedChangeListener)
	}
}