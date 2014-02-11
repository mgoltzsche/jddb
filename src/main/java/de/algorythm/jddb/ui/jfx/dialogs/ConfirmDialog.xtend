package de.algorythm.jddb.ui.jfx.dialogs

import de.algorythm.jddb.bundle.Bundle
import de.algorythm.jddb.bundle.ImageBundle
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage

import static javafx.application.Platform.*

class ConfirmDialog {
	
	static val bundle = Bundle.instance
	val Stage primaryStage
	var Stage stage
	val label = new Label
	val vBox = new VBox(7)
	var boolean result
	
	new(Stage primaryStage) {
		this.primaryStage = primaryStage	
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
	}
	
	def private getOrCreateStage() {
		if (stage == null) {
			stage = new Stage
			stage.initOwner(primaryStage)
			stage.initModality(Modality.APPLICATION_MODAL)
			stage.resizable = false
			stage.scene = new Scene(vBox)
		}
		
		stage
	}
	
	def confirm(String message) {
		val stage = getOrCreateStage
		label.text = message
		stage.centerOnScreen
		stage.showAndWait
		result
	}
}