package de.algorythm.jdoe.ui.jfx.cell;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public class ReferringEntityCell extends ListCell<FXEntityReference> {
	
	static public final Callback<ListView<FXEntityReference>, ListCell<FXEntityReference>> FACTORY = new Callback<ListView<FXEntityReference>, ListCell<FXEntityReference>>() {
		@Override
		public ListCell<FXEntityReference> call(final ListView<FXEntityReference> view) {
			return new ReferringEntityCell();
		}
	};
	
	private ReferringEntityCell() {}
	
	@Override
	public void updateItem(final FXEntityReference entity, final boolean empty) {
		super.updateItem(entity, empty);
		
		if (empty)
			textProperty().unbind();
		else
			textProperty().bind(entity.labelProperty());
	}
}