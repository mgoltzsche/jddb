package de.algorythm.jddb.ui.jfx.dialogs

import de.algorythm.jddb.bundle.Bundle
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage

import static javafx.application.Platform.*
import javafx.geometry.Insets
import javafx.scene.image.ImageView
import de.algorythm.jddb.bundle.ImageBundle

class ConfirmDialog {
	
	static val bundle = Bundle.instance
	val stage = new Stage
	val label = new Label
	var boolean result
	
	new(Stage primaryStage) {		
		val vBox = new VBox(7)
		val labelBox = new HBox(7)
		val btnBox = new HBox(7)
		val okButton = new Button(bundle.yes) => [
			setOnAction [
				result = true
				stage.hide
			]
		]
		val cancelButton = new Button(bundle.no) => [
			runLater [|
				requestFocus
			]
			setOnAction [
				result = false
				stage.hide
			]
		]
		
		vBox.padding = new Insets(7)
		labelBox.children += new ImageView(ImageBundle.instance.warn)
		labelBox.children += label
		btnBox.alignment = Pos.CENTER
		btnBox.children += okButton
		btnBox.children += cancelButton
		vBox.children += labelBox
		vBox.children += btnBox
		
		stage.initOwner(primaryStage)
		stage.initModality(Modality.APPLICATION_MODAL)
		stage.resizable = false
		stage.scene = new Scene(vBox)
	}
	
	def confirm(String message) {
		label.text = message
		stage.centerOnScreen
		stage.showAndWait
		result
	}
}