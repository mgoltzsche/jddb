package de.algorythm.jdoe.ui.jfx.cell;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import de.algorythm.jdoe.taskQueue.TaskState;
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTask;

public class TaskCell extends ListCell<FXTask> {

	static public final Callback<ListView<FXTask>, ListCell<FXTask>> FACTORY = new Callback<ListView<FXTask>, ListCell<FXTask>>() {
		@Override
		public ListCell<FXTask> call(ListView<FXTask> arg0) {
			return new TaskCell();
		}
	};
	
	
	private final HBox box = new HBox();
	private final Label titleLabel = new Label();
	private final ProgressIndicator progressIndicator = new ProgressIndicator();
	private final Region spacer = new Region();
	
	protected TaskCell() {
		final ObservableList<Node> children = box.getChildren();
		
		HBox.setHgrow(spacer, Priority.ALWAYS);
		box.setAlignment(Pos.CENTER_LEFT);
		box.setFillHeight(true);
		progressIndicator.setPrefSize(20, 20);
		progressIndicator.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		
		children.add(titleLabel);
		children.add(spacer);
		children.add(progressIndicator);
		setGraphic(box);
	}
	
	@Override
	public void updateItem(final FXTask task, final boolean empty) {
		super.updateItem(task, empty);
		
		if (empty) {
			titleLabel.setText(null);
			progressIndicator.visibleProperty().unbind();
			progressIndicator.setVisible(false);
		} else {
			titleLabel.setText(task.getLabel());
			progressIndicator.visibleProperty().bind(task.stateProperty().isEqualTo(TaskState.RUNNING));
		}
	}
}
