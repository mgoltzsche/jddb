package de.algorythm.jdoe.ui.jfx.cell;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public class AssociationContainmentCell extends ListCell<FXEntityReference> {

	static public final Callback<ListView<FXEntityReference>, ListCell<FXEntityReference>> FACTORY = new Callback<ListView<FXEntityReference>, ListCell<FXEntityReference>>() {

		@Override
		public ListCell<FXEntityReference> call(final ListView<FXEntityReference> view) {
			return new AssociationContainmentCell();
		}
	};
	
	
	private final Label label = new Label();
	private final Button removeButton = new Button("remove");
	private final BorderPane pane = new BorderPane();
	private FXEntityReference entity;
	
	public AssociationContainmentCell() {
		BorderPane.setAlignment(label, Pos.CENTER_LEFT);
		
		pane.setCenter(label);
		pane.setRight(removeButton);
		
		removeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				getListView().getItems().remove(entity);
			}
		});
	}
	
	@Override
	public void updateItem(FXEntityReference entity, boolean empty) {
		super.updateItem(entity, empty);
		
		this.entity = entity;
		
		if (empty) {
			label.textProperty().unbind();
		} else {
			label.textProperty().bind(entity.labelProperty());
			setGraphic(pane);
		}
	}
}
