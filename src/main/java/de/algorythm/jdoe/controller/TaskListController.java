package de.algorythm.jdoe.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import de.algorythm.jdoe.ui.jfx.taskQueue.FXTask;
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

public class TaskListController implements Initializable {

	@Inject private FXTaskQueue taskQueue;
	@FXML private ListView<FXTask> taskList;
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		//taskList.setCellFactory(arg0); // TODO: continue
	}
}
