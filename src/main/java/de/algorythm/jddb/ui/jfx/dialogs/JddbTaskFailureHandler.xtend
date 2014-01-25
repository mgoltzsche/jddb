package de.algorythm.jddb.ui.jfx.dialogs

import de.algorythm.jddb.bundle.Bundle
import de.algorythm.jddb.taskQueue.ITaskQueueExceptionHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage

import static javafx.application.Platform.*

class JddbTaskFailureHandler implements ITaskQueueExceptionHandler {
	
	val Stage primaryStage
	
	new(Stage primaryStage) {
		this.primaryStage = primaryStage
	}
	
	override handleError(Throwable e) {
		val msg = e.message
		val errorMessage = if (msg == null || msg.replaceAll('[\\s]+', '').empty)
				'''An unexpected error of type «e.getClass.simpleName» occured!'''
			else
				msg
		
		runLater [|
			val stage = new Stage
			val vBox = new VBox(5)
			val label = new Label(errorMessage)
			val closeBtn = new Button(Bundle.instance.ok)
			
			closeBtn.setOnAction [
				stage.close
			]
			
			vBox.padding = new Insets(5, 5, 5, 5)
			vBox.alignment = Pos.CENTER
			
			vBox.children += label
			vBox.children += closeBtn
			
			//closeBtn.alignment = Pos.BASELINE_CENTER
			
			stage.initModality(Modality.APPLICATION_MODAL)
			stage.initOwner(primaryStage)
			stage.resizable = false
			stage.scene = new Scene(vBox)
			stage.title = 'ERROR'
			stage.scene.stylesheets += getClass.getResource('/styles/color.css').toExternalForm
			
			stage.showAndWait
		]
	}
}