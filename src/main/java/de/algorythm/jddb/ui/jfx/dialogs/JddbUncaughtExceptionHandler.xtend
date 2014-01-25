package de.algorythm.jddb.ui.jfx.dialogs

import de.algorythm.jddb.bundle.Bundle
import java.io.PrintWriter
import java.io.StringWriter
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import org.slf4j.LoggerFactory

import static javafx.application.Platform.*
import java.lang.Thread.UncaughtExceptionHandler

public class JddbUncaughtExceptionHandler implements UncaughtExceptionHandler {

	static val log = LoggerFactory.getLogger(typeof(JddbUncaughtExceptionHandler))

	val bundle = Bundle.instance
	val Stage primaryStage
	
	new(Stage primaryStage) {
		this.primaryStage = primaryStage
	}
	
	override uncaughtException(Thread t, Throwable e) {
		log.error('uncaught exception', e)
		
		runLater [|
			val stage = new Stage
			val vBox = new VBox(5)
			val hBox = new HBox
			val label = new Label('Sorry, Java Desktop Database must be terminated due to a fatal error:')
			val infoArea = new TextArea
			val stackTraceWriter = new StringWriter
			val closeBtn = new Button(bundle.close)
			
			closeBtn.setOnAction [
				System.exit(1)
			]
			
			e.printStackTrace(new PrintWriter(stackTraceWriter))
			infoArea.editable = false
			infoArea.text = stackTraceWriter.toString
			vBox.padding = new Insets(5, 5, 5, 5)
			hBox.padding = new Insets(5, 5, 5, 5)
			hBox.alignment = Pos.BASELINE_CENTER
			
			hBox.children += closeBtn
			vBox.children += label
			vBox.children += infoArea
			vBox.children += hBox
			
			closeBtn.alignment = Pos.BASELINE_CENTER
			
			stage.initModality(Modality.APPLICATION_MODAL)
			stage.initOwner(primaryStage)
			stage.scene = new Scene(vBox)
			stage.title = 'FATAL ERROR'
			stage.scene.stylesheets += getClass.getResource('/styles/color.css').toExternalForm
			
			stage.showAndWait
		]
	}
}
