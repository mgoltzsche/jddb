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

class ConfirmDialog {
	
	static val bundle = Bundle.instance
	val stage = new Stage
	val label = new Label
	var boolean result
	
	new(Stage primaryStage) {		
		val vBox = new VBox(7)
		val hBox = new HBox(7)
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
		hBox.alignment = Pos.CENTER
		hBox.children += okButton
		hBox.children += cancelButton
		vBox.children += label
		vBox.children += hBox
		
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