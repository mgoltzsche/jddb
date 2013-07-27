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
import de.algorythm.jdoe.model.entity.IEntity;

public class AssociationCell extends ListCell<IEntity> {

	static public final Callback<ListView<IEntity>, ListCell<IEntity>> FACTORY = new Callback<ListView<IEntity>, ListCell<IEntity>>() {

		@Override
		public ListCell<IEntity> call(final ListView<IEntity> view) {
			return new AssociationCell();
		}
	};
	
	
	private final Label label = new Label();
	private final Button btn = new Button("remove");
	private final BorderPane pane = new BorderPane();
	private IEntity entity;
	
	public AssociationCell() {
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
	public void updateItem(IEntity entity, boolean empty) {
		super.updateItem(entity, empty);
		
		this.entity = entity;
		
		if (!empty) {
			label.setText(entity.toString());
			setGraphic(pane);
		}
	}
}
