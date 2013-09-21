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
import javafx.scene.layout.StackPane
import javafx.event.EventHandler

class StatusController implements Initializable, ListChangeListener<FXTask> {
	
	@Inject extension FXTaskQueue
	@Inject extension GuiceFxmlLoader
	@Inject Bundle bundle
	@FXML StackPane statusPane
	@FXML Label status
	@FXML ProgressBar progress
	val failedTasks = new SimpleListProperty(FXCollections.observableList(new LinkedList<FXTask>))
	var taskCount = 0
	var taskFinishedCount = 0
	val taskDetails = new Stage
	
	override initialize(URL url, ResourceBundle resourceBundle) {
		val EventHandler<MouseEvent> statusClickListener = [
			showTaskDetails
		]
		
		statusPane.addEventHandler(MouseEvent.MOUSE_CLICKED, statusClickListener)
		progress.addEventHandler(MouseEvent.MOUSE_CLICKED, statusClickListener) 
		
		taskListProperty.addListener(this)
		
		status.text = bundle.stateReady
		
		// setup task details window
		val FxmlLoaderResult<Parent, TaskListController> loaderResult = load('/fxml/task_list.fxml')
		
		loaderResult.controller.init(taskListProperty, failedTasks)
		
		taskDetails.title = bundle.taskDetails
		taskDetails.scene = new Scene(loaderResult.node, 500, 300)
	}
	
	def private showTaskDetails() {
		if (taskDetails.showing)
			taskDetails.hide
		
		taskDetails.show
	}
	
	override onChanged(Change<? extends FXTask> change) {
		while (change.next) {
			taskCount = taskCount + change.addedSize
			
			for (task : change.removed) {
				if (task.state == TaskState.FAILED)
					failedTasks += task
				
				taskFinishedCount = taskFinishedCount + 1
			}
		}
		
		val tasks = taskListProperty.value
		
		if (tasks.empty) {
			status.text = bundle.stateReady
			
			// reset progress
			taskCount = 0
			taskFinishedCount = 0
		} else {
			status.text = tasks.head.label
		}
		 
		progress.progress = if (taskCount == 0)
			1
		else
			taskFinishedCount / taskCount as double
	}
	
}