package de.algorythm.jddb.ui.jfx.taskQueue

import de.algorythm.jddb.taskQueue.AbstractTaskQueue
import de.algorythm.jddb.taskQueue.ITaskPriority
import de.algorythm.jddb.taskQueue.ITaskResult
import java.util.ArrayList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0

import static javafx.application.Platform.*

class FXTaskQueue extends AbstractTaskQueue<FXTask> {
	
	static val pendingTasksProperty = new SimpleListProperty<FXTask>(FXCollections.observableList(new ArrayList<FXTask>))
	
	def static ReadOnlyListProperty<FXTask> pendingTasksProperty() {
		pendingTasksProperty
	}
	
	new(String name) {
		super(name)
	}
	
	def ITaskResult runTask(String id, String label, ITaskPriority priority, Procedure0 task) {
		val fxTask = new FXTask(id, label, priority, task)
		
		runTask(fxTask)
		
		fxTask
	}
	
	override onTaskQueued(FXTask task) {
		runLater [|
			task.priority.add(task, pendingTasksProperty)
		]
	}
	
	override onTaskRemoved(FXTask task) {
		runLater [|
			pendingTasksProperty.remove(task)
		]
	}
}