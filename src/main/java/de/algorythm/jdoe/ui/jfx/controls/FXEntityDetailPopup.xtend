package de.algorythm.jdoe.ui.jfx.controls

import de.algorythm.jdoe.ui.jfx.model.FXEntity
import javafx.event.EventHandler
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.input.MouseEvent
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

import static javafx.application.Platform.*
import javafx.scene.layout.VBox
import javafx.scene.layout.HBox
import javafx.scene.control.Label
import java.util.LinkedList
import javafx.scene.image.ImageView
import de.algorythm.jdoe.ui.jfx.util.FilePropertyUtil
import javafx.beans.property.SimpleStringProperty
import de.algorythm.jdoe.ui.jfx.loader.image.ImageLoader
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference

class FXEntityDetailPopup extends ContextMenu {

	static val PREVIEW_IMAGE_COUNT = 3

	extension FilePropertyUtil = new FilePropertyUtil
	extension ImageLoader = ImageLoader.instance
	val vBox = new VBox
	val hBox = new HBox
	val label = new Label
	val filePathProperties = new LinkedList<SimpleStringProperty>
	var Node currentTargetNode
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
			
			filePathProperties += filePathProperty
			hBox.children += imageView
			
			imageView.bindImage(filePathProperty, showingProperty)
		}
		
		item.graphic = vBox
		items += item
		
		/*addEventHandler(MouseEvent.MOUSE_ENTERED) [
			hide
		]*/
	}
	
	def show(Node node, Procedure1<Procedure1<FXEntity>> loader) {
		currentTargetNode = node
		entity = null
		hide
		
		loader.apply [
			runLater [|
				if (currentTargetNode == node)
					node.show(it)
			]
		]
	}
	
	def show(Node node, FXEntity entity) {
		currentTargetNode = null
		this.entity = entity
		
		show(node, Side.BOTTOM, 3, 0)
		node.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseExitHandler)
		node.addEventHandler(MouseEvent.MOUSE_EXITED, mouseExitHandler)
	}
	
	def private setEntity(FXEntity entity) {
		if (entity == null) {
			label.text = null
			
			for (filePathProperty : filePathProperties)
				filePathProperty.value = null
		} else {
			label.text = entity.toString
			
			val filePathPropertyIter = filePathProperties.iterator
			val pathIter = entity.filePathes.iterator
			
			while (filePathPropertyIter.hasNext) {
				if (pathIter.hasNext)
					filePathPropertyIter.next.value = pathIter.next
				else
					filePathPropertyIter.next.value = null
			}
		}
	}
}