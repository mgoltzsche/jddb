package de.algorythm.jdoe.ui.jfx.cell;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import de.algorythm.jdoe.JavaDbObjectEditorFacade;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public class AssociationCell extends ListCell<FXEntityReference> {

	static public class Factory implements Callback<ListView<FXEntityReference>, ListCell<FXEntityReference>> {

		private final JavaDbObjectEditorFacade facade;
		
		public Factory(final JavaDbObjectEditorFacade facade) {
			this.facade = facade;
		}
		
		@Override
		public ListCell<FXEntityReference> call(final ListView<FXEntityReference> view) {
			return new AssociationCell(facade);
		}
	};
	
	
	private final HBox hBox = new HBox();
	private final Label label = new Label();
	private final Region spacer = new Region();
	private final Button removeButton = new Button("remove");
	private FXEntityReference entity;
	
	public AssociationCell(final JavaDbObjectEditorFacade facade) {
		HBox.setHgrow(spacer, Priority.ALWAYS);
		hBox.setAlignment(Pos.CENTER_LEFT);
		
		final ObservableList<Node> hBoxChildren = hBox.getChildren();
		
		hBoxChildren.add(label);
		hBoxChildren.add(spacer);
		hBoxChildren.add(removeButton);
		
		removeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				getListView().getItems().remove(entity);
			}
		});
		
		addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent evt) {
				facade.showEntityEditor(entity);
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
			setGraphic(hBox);
		}
	}
}
