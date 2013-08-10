package de.algorythm.jdoe.ui.jfx.cell;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;

public class AssociationCell extends ListCell<FXEntity> {
	
	static public final Callback<ListView<FXEntity>, ListCell<FXEntity>> FACTORY = new Callback<ListView<FXEntity>, ListCell<FXEntity>>() {
		@Override
		public ListCell<FXEntity> call(final ListView<FXEntity> view) {
			return new AssociationCell();
		}
	};
	
	@Override
	public void updateItem(FXEntity entity, boolean empty) {
		super.updateItem(entity, empty);
		
		if (empty)
			textProperty().unbind();
		else
			textProperty().bind(entity.getLabel());
	}
}
