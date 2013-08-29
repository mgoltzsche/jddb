package de.algorythm.jdoe.controller;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import de.algorythm.jdoe.ui.jfx.cell.TaskCell;
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTask;

public class TaskListController {

	@FXML private ListView<FXTask> taskQueuedList;
	@FXML private ListView<FXTask> taskFailedList;
	
	public void init(final ReadOnlyListProperty<FXTask> pendingTasks, final ReadOnlyListProperty<FXTask> failedTasks) {
		taskQueuedList.setCellFactory(TaskCell.FACTORY);
		taskFailedList.setCellFactory(TaskCell.FACTORY);
		
		taskQueuedList.itemsProperty().bind(pendingTasks);
		taskFailedList.itemsProperty().bind(failedTasks);
	}
}
