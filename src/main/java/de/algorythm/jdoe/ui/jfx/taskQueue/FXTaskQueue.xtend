package de.algorythm.jdoe.ui.jfx.taskQueue

import de.algorythm.jdoe.taskQueue.AbstractTaskQueue
import java.util.LinkedList
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javax.inject.Singleton
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0
import static extension javafx.application.Platform.*

@Singleton
class FXTaskQueue extends AbstractTaskQueue<FXTask> {
	
	val tasks = new SimpleListProperty<FXTask>(FXCollections.observableList(new LinkedList<FXTask>()));
	
	override runTask(FXTask task) {
		super.runTask(task)
		
		val currentTasks = new LinkedList(taskMap.values);
		
		runLater [|
			tasks.setAll(currentTasks);
		]
	}
	
	def runTask(String id, String label, Procedure0 task) {
		runTask(new FXTask(id, label, task));
	}
	
	def ReadOnlyListProperty<FXTask> tasksProperty() {
		return tasks;
	}

	override onTaskFinished(FXTask task) {
		runLater [|
			tasks.remove(task);
		]
	}
}
