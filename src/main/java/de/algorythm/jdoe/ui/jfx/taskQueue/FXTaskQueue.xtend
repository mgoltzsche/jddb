package de.algorythm.jdoe.ui.jfx.taskQueue

import de.algorythm.jdoe.taskQueue.AbstractTaskQueue
import java.util.LinkedList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javax.inject.Singleton
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0

import static javafx.application.Platform.*

@Singleton
class FXTaskQueue extends AbstractTaskQueue<FXTask> {
	
	val taskList = new SimpleListProperty<FXTask>(FXCollections.observableList(new LinkedList<FXTask>))
	
	def runTask(String id, String label, Procedure0 task) {
		runTask(new FXTask(id, label, task))
	}
	
	def ReadOnlyListProperty<FXTask> taskListProperty() {
		taskList
	}

	override onTaskQueued(FXTask task) {
		updateTasks
	}

	override onTaskFinished(FXTask task) {
		updateTasks
	}
	
	def private updateTasks() {
		val currentTasks = new LinkedList(taskMap.values)
		
		runLater [|
			taskList.all = currentTasks
		]
	}
}