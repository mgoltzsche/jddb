package de.algorythm.jddb.controller

import javafx.beans.property.ReadOnlyListProperty
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ListView
import de.algorythm.jddb.ui.jfx.cell.TaskCell
import de.algorythm.jddb.ui.jfx.taskQueue.FXTask

public class TaskListController {

	@FXML var ListView<FXTask> pendingList
	@FXML var ListView<FXTask> errorList
	@FXML var Label errorMessageLabel
	
	def init(ReadOnlyListProperty<FXTask> pendingTasks, ReadOnlyListProperty<FXTask> failedTasks) {
		pendingList.cellFactory = TaskCell.FACTORY
		errorList.cellFactory = TaskCell.FACTORY
		
		pendingList.itemsProperty.bind(pendingTasks)
		errorList.itemsProperty.bind(failedTasks)
		
		errorList.selectionModel.selectedItemProperty.addListener [c,o,v|
			errorMessageLabel.text = v.errorMessage
		]
	}
}
