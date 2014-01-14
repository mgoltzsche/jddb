package de.algorythm.jdoe.ui.jfx.cell;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import de.algorythm.jdoe.ui.jfx.model.meta.IFXPropertyType;

public class PropertyTypeSelectionCell extends ListCell<IFXPropertyType> {

	static public final Callback<ListView<IFXPropertyType>, ListCell<IFXPropertyType>> FACTORY = new Callback<ListView<IFXPropertyType>, ListCell<IFXPropertyType>>() {
		@Override
		public ListCell<IFXPropertyType> call(final ListView<IFXPropertyType> view) {
			return new PropertyTypeSelectionCell();
		}
	};
	
	@Override
	public void updateItem(final IFXPropertyType type, final boolean empty) {
		super.updateItem(type, empty);
		
		if (empty)
			textProperty().unbind();
		else
			textProperty().bind(type.labelProperty());
	}
}
