package de.algorythm.jdoe.ui.jfx.controls

import de.algorythm.jdoe.ui.jfx.loader.image.ImageLoader
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.util.FilePropertyUtil
import java.util.LinkedList
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

import static javafx.application.Platform.*

class FXEntityDetailPopup extends ContextMenu {

	static val PREVIEW_IMAGE_COUNT = 3

	extension FilePropertyUtil = new FilePropertyUtil
	extension ImageLoader = ImageLoader.instance
	val vBox = new VBox
	val hBox = new HBox(5)
	val label = new Label
	val filePathProperties = new LinkedList<SimpleStringProperty>
	val EventHandler<? super MouseEvent> mouseExitHandler = [
		hide
	]
	
	new() {
		super()
		
		val item = new MenuItem
		vBox.children += label
		vBox.children += hBox
		
		for (i : 1..PREVIEW_IMAGE_COUNT) {
			val filePathProperty = new SimpleStringProperty
			val imageView = new ImageView
			imageView.cache = false
			
			filePathProperties += filePathProperty
			hBox.children += imageView
			
			imageView.bindImage(filePathProperty, showingProperty)
		}
		
		item.graphic = vBox
		items += item
	}
	
	def show(Node node, FXEntityReference entityRef) {
		runLater [|
			this.entityRef = entityRef
			
			show(node, Side.BOTTOM, 3, 0)
			node.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseExitHandler)
			node.addEventHandler(MouseEvent.MOUSE_EXITED, mouseExitHandler)
		]
	}
	
	def private setEntityRef(FXEntityReference entityRef) {
		if (entityRef == null) {
			label.text = null
			
			for (filePathProperty : filePathProperties)
				filePathProperty.value = null
		} else {
			label.text = entityRef.toString
			
			val filePathPropertyIter = filePathProperties.iterator
			val pathIter = entityRef.filePathes.iterator
			
			while (filePathPropertyIter.hasNext) {
				val filePathProperty = filePathPropertyIter.next
				
				filePathProperty.value = null
				
				if (pathIter.hasNext) {
					val newPath = pathIter.next
					
					filePathProperty.value = newPath
				} 
			}
		}
	}
}