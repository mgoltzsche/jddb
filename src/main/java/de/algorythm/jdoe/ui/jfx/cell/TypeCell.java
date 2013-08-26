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
import de.algorythm.jdoe.ui.jfx.model.meta.FXType;

public class TypeCell extends AbstractLabeledListCell<FXType> {
	
	static private final Bundle bundle = Bundle.getInstance();
	
	static public class Factory implements Callback<ListView<FXType>, ListCell<FXType>> {
	
		@Override
		public ListCell<FXType> call(ListView<FXType> view) {
			return new TypeCell();
		}
	}
	
	
	private VBox vBox = new VBox();
	private CheckBox embedded = new CheckBox();
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
		
		embedded.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> valueContainer,
					Boolean oldValue, Boolean newValue) {
				if (object != null)
					object.setEmbedded(newValue);
			}
		});
	}
	
	@Override
	public void updateItem(FXType type, boolean empty) {
		super.updateItem(object, empty);
		
		if (type != null)
			embedded.selectedProperty().set(type.isEmbedded());
	}
	
	protected void showEditor() {
		setGraphic(vBox);
	}
}
