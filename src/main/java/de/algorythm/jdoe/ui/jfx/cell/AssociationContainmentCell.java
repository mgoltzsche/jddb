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
import de.algorythm.jdoe.ui.jfx.model.FXEntity;

public class AssociationContainmentCell extends ListCell<FXEntity> {

	static public final Callback<ListView<FXEntity>, ListCell<FXEntity>> FACTORY = new Callback<ListView<FXEntity>, ListCell<FXEntity>>() {

		@Override
		public ListCell<FXEntity> call(final ListView<FXEntity> view) {
			return new AssociationContainmentCell();
		}
	};
	
	
	private final Label label = new Label();
	private final Button btn = new Button("remove");
	private final BorderPane pane = new BorderPane();
	private FXEntity entity;
	
	public AssociationContainmentCell() {
		BorderPane.setAlignment(label, Pos.CENTER_LEFT);
		
		pane.setCenter(label);
		pane.setRight(btn);
		
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				getListView().getItems().remove(entity);
			}
		});
	}
	
	@Override
	public void updateItem(FXEntity entity, boolean empty) {
		super.updateItem(entity, empty);
		
		this.entity = entity;
		
		if (empty) {
			label.textProperty().unbind();
		} else {
			label.textProperty().bind(entity.getLabel());
			setGraphic(pane);
		}
	}
}
