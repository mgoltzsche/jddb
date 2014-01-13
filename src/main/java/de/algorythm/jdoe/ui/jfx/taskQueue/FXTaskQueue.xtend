package de.algorythm.jdoe.ui.jfx.taskQueue

import de.algorythm.jdoe.taskQueue.AbstractTaskQueue
import de.algorythm.jdoe.taskQueue.ITaskResult
import de.algorythm.jdoe.taskQueue.TaskState
import java.util.HashMap
import java.util.LinkedList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0

import static javafx.application.Platform.*

class FXTaskQueue extends AbstractTaskQueue<FXTask> {
	static val INSTANCE_MAP = new HashMap<String, FXTaskQueue>
	
	def static Iterable<FXTaskQueue> getInstances() {
		INSTANCE_MAP.values
	}
	
	
	val taskList = new SimpleListProperty<FXTask>(FXCollections.observableList(new LinkedList<FXTask>))
	
	new(String name) {
		super(name)
		INSTANCE_MAP.put(name, this)
	}
	
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