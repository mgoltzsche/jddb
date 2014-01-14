package de.algorythm.jdoe.controller

import de.algorythm.jdoe.bundle.Bundle
import de.algorythm.jdoe.taskQueue.TaskState
import de.algorythm.jdoe.ui.jfx.loader.fxml.FxmlLoaderResult
import de.algorythm.jdoe.ui.jfx.loader.fxml.GuiceFxmlLoader
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTask
import java.net.URL
import java.util.LinkedList
import java.util.ResourceBundle
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ListChangeListener.Change
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javax.inject.Inject

import static de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue.*

class StatusController implements Initializable {
	
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
		
		pendingTasksProperty.addListener([Change<? extends FXTask> it|
			taskListChanged
		] as ListChangeListener<FXTask>)
		
		status.text = bundle.stateReady
		
		// setup task details window
		val FxmlLoaderResult<Parent, TaskListController> loaderResult = load('/fxml/task_list.fxml')
		
		loaderResult.controller.init(pendingTasksProperty, failedTasks)
		
		taskDetails.title = bundle.taskDetails
		taskDetails.scene = new Scene(loaderResult.node, 500, 300)
	}
	
	def private showTaskDetails() {
		if (taskDetails.showing)
			taskDetails.hide
		
		taskDetails.show
	}
	
	def private void taskListChanged(Change<? extends FXTask> it) {
		while (next) {
			taskCount = taskCount + addedSize
			
			for (task : removed) {
				if (task.state == TaskState.FAILED && !failedTasks.contains(task))
					failedTasks += task
				
				taskFinishedCount = taskFinishedCount + 1
			}
		}
		
		val tasks = pendingTasksProperty.value
		
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