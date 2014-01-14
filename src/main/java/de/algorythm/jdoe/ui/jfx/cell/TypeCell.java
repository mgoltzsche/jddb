package de.algorythm.jdoe.ui.jfx.cell;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import de.algorythm.jdoe.bundle.Bundle;
import de.algorythm.jdoe.ui.jfx.model.meta.FXEntityType;

public class TypeCell extends AbstractLabeledListCell<FXEntityType> implements ChangeListener<Boolean> {
	
	static private final Bundle bundle = Bundle.getInstance();
	
	static public class Factory implements Callback<ListView<FXEntityType>, ListCell<FXEntityType>> {
	
		@Override
		public ListCell<FXEntityType> call(ListView<FXEntityType> view) {
			return new TypeCell();
		}
	}
	
	
	private VBox vBox = new VBox();
	private CheckBox embedded = new CheckBox(bundle.embedded);
	private Button deleteButton = new Button(bundle.delete);
	
	public TypeCell() {
		super();
		
		final ObservableList<Node> vBoxChildren = vBox.getChildren();
		
		vBoxChildren.add(labelInput);
		vBoxChildren.add(embedded);
		vBoxChildren.add(deleteButton);
		
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				getListView().getItems().remove(object);
			}
		});
	}
	
	@Override
	public void changed(ObservableValue<? extends Boolean> valueContainer,
			Boolean oldValue, Boolean newValue) {
		if (object != null)
			object.setEmbedded(newValue);
	}
	
	@Override
	public void updateItem(FXEntityType type, boolean empty) {
		super.updateItem(type, empty);
		
		if (type != null) {
			embedded.selectedProperty().removeListener(this);
			embedded.selectedProperty().set(type.isEmbedded());
			embedded.selectedProperty().addListener(this);
		}
	}
	
	protected void showEditor() {
		setGraphic(vBox);
	}
}
