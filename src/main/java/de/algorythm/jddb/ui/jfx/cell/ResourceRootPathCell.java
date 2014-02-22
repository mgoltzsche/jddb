package de.algorythm.jddb.ui.jfx.cell;

import java.io.File;

import de.algorythm.jddb.bundle.Bundle;
import de.algorythm.jddb.bundle.ImageBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;

public class ResourceRootPathCell extends ListCell<File> {
	
	static public final Callback<ListView<File>, ListCell<File>> FACTORY = new Callback<ListView<File>, ListCell<File>>() {
		@Override
		public ListCell<File> call(final ListView<File> view) {
			return new ResourceRootPathCell();
		}
	};
	
	
	private final HBox box = new HBox(7);
	private final Label label = new Label();
	private final Button rmButton = new Button();
	
	private ResourceRootPathCell() {
		final ObservableList<Node> children = box.getChildren();
		final Region spacer = new Region();
		
		HBox.setHgrow(spacer, Priority.ALWAYS);
		label.setAlignment(Pos.CENTER_LEFT);
		rmButton.setGraphic(new ImageView(ImageBundle.getInstance().delete));
		rmButton.setTooltip(new Tooltip(Bundle.getInstance().remove));
		
		children.add(label);
		children.add(rmButton);
		
		rmButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent evt) {
				getListView().getItems().remove(getItem());
			}
		});
	}
	
	@Override
	public void updateItem(final File rootDirectory, final boolean empty) {
		super.updateItem(rootDirectory, empty);
		
		if (!empty) {
			label.setText(rootDirectory.getAbsolutePath());
			rmButton.setVisible(getListView().getItems().size() > 1);
			setGraphic(box);
		}
	}
}