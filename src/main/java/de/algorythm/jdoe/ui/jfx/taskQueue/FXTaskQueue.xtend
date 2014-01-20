package de.algorythm.jdoe.ui.jfx.taskQueue

import de.algorythm.jdoe.taskQueue.AbstractTaskQueue
import de.algorythm.jdoe.taskQueue.ITaskPriority
import de.algorythm.jdoe.taskQueue.ITaskResult
import java.util.LinkedList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0

import static javafx.application.Platform.*

class FXTaskQueue extends AbstractTaskQueue<FXTask> {
	
	static val pendingTasks = new SimpleListProperty<FXTask>(FXCollections.observableList(new LinkedList<FXTask>))
	
	def static ReadOnlyListProperty<FXTask> pendingTasksProperty() {
		pendingTasks
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
			pendingTasks.remove(task)
			//FXCollections.sort(pendingTasks, FXTaskStateComparator.INSTANCE);
			task.priority.add(task, pendingTasks)
		]
	}
	
	override onTaskRemoved(FXTask task) {
		runLater [|
			pendingTasks.remove(task)
			//FXCollections.sort(pendingTasks, FXTaskStateComparator.INSTANCE);
		]
	}
}