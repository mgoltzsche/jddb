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
import de.algorythm.jddb.taskQueue.ITaskQueueExceptionHandler
import org.slf4j.LoggerFactory

class FXTaskQueue extends AbstractTaskQueue<FXTask> {
	
	static val log = LoggerFactory.getLogger(typeof(FXTaskQueue))
	
	static val pendingTasksProperty = new SimpleListProperty<FXTask>(FXCollections.observableList(new ArrayList<FXTask>))
	
	def static ReadOnlyListProperty<FXTask> pendingTasksProperty() {
		pendingTasksProperty
	}
	
	
	new(String name, ITaskQueueExceptionHandler exceptionHandler) {
		super(name, exceptionHandler)
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
		try {
			runLater [|
				pendingTasksProperty.remove(task)
			]
		} catch(IllegalStateException e) {
			log.debug('Cannot register deletion pendingTasksProperty entry', e)
		}
	}
}