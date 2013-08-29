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
import javafx.collections.ListChangeListener
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTask
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import java.util.LinkedList
import de.algorythm.jdoe.taskQueue.TaskState
import de.algorythm.jdoe.ui.jfx.util.FxmlLoaderResult
import javafx.scene.Parent
import de.algorythm.jdoe.ui.jfx.util.GuiceFxmlLoader
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.collections.ListChangeListener.Change
import javafx.scene.input.MouseEvent
import java.beans.EventHandler
import javafx.event.EventType

class StatusController implements Initializable, ListChangeListener<FXTask> {
	
	@Inject extension FXTaskQueue
	@Inject extension GuiceFxmlLoader
	@Inject Bundle bundle
	@FXML Label status
	@FXML ProgressBar progress
	val failedTasks = new SimpleListProperty(FXCollections.observableList(new LinkedList<FXTask>))
	
	override initialize(URL url, ResourceBundle resourceBundle) {
		progress.addEventHandler(MouseEvent.MOUSE_CLICKED) [
			showDetails
		]
		
		taskListProperty.addListener(this)
	}
	
	def private showDetails() {
		showTaskList(bundle.tasksQueued)
	}
	
	def private showErrors() {
		showTaskList(bundle.tasksFailed)
	}
	
	def showTaskList(String title) {
		val FxmlLoaderResult<Parent, Object> loaderResult = load('/fxml/task_list.fxml')
		val stage = new Stage
		
		stage.title = title
		stage.scene = new Scene(loaderResult.node, 500, 300)
		stage.show
	}
	
	override onChanged(Change<? extends FXTask> change) {
		while (change.next)
			for (failedTask : change.removed.filter[t|t.state == TaskState.FAILED])
				failedTasks += failedTask
		
		val tasks = taskListProperty.value
		
		if (tasks.empty) {
			progress.visible = false
			status.text = bundle.stateReady
		} else {
			progress.visible = true
			status.text = tasks.head.label
		}
	}
	
}