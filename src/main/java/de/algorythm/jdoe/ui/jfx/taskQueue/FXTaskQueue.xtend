package de.algorythm.jdoe.ui.jfx.taskQueue

import de.algorythm.jdoe.taskQueue.AbstractTaskQueue
import java.util.LinkedList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javax.inject.Singleton
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0

import static javafx.application.Platform.*
import de.algorythm.jdoe.taskQueue.TaskState
import de.algorythm.jdoe.taskQueue.ITaskResult

@Singleton
class FXTaskQueue extends AbstractTaskQueue<FXTask> {
	
	val taskList = new SimpleListProperty<FXTask>(FXCollections.observableList(new LinkedList<FXTask>))
	
	def ITaskResult runTask(String id, String label, Procedure0 task) {
		val fxTask = new FXTask(id, label, task)
		runTask(fxTask)
		fxTask
	}
	
	def ReadOnlyListProperty<FXTask> taskListProperty() {
		taskList
	}

	override onTaskQueued(FXTask task) {
		updateTasks
	}

	override onTaskStarted(FXTask task) {
		runLater [|
			super.onTaskStarted(task)
		]
	}
	
	override onTaskFinished(FXTask task, TaskState state) {
		runLater [|
			super.onTaskFinished(task, state)
			updateTasks
		]
	}
	
	def private updateTasks() {
		val pendingTasks = getPendingTasks();
		
		runLater [|
			taskList.all = pendingTasks
		]
	}
}