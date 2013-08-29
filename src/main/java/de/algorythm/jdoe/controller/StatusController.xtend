package de.algorythm.jdoe.controller

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.fxml.Initializable
import java.net.URL
import java.util.ResourceBundle
import javax.inject.Inject
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue
import de.algorythm.jdoe.bundle.Bundle

class StatusController implements Initializable {
	
	@Inject extension FXTaskQueue
	@Inject Bundle bundle
	@FXML Label status
	@FXML ProgressBar progress
	
	override initialize(URL url, ResourceBundle resourceBundle) {
		tasksProperty.addListener [
			val tasks = tasksProperty.value
			
			if (tasks.empty) {
				progress.visible = false
				status.text = bundle.stateReady
			} else {
				progress.visible = true
				status.text = tasks.head.label
			}
		]
	}
}