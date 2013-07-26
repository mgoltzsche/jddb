package de.algorythm.jdoe.ui.jfx.cell;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import de.algorythm.jdoe.model.meta.IPropertyType;
import de.algorythm.jdoe.ui.jfx.model.FXPropertyType;

public class PropertyTypeSelectionCell extends ListCell<FXPropertyType<? extends IPropertyType>> {

	static public final Callback<ListView<FXPropertyType<? extends IPropertyType>>, ListCell<FXPropertyType<? extends IPropertyType>>> FACTORY = new Callback<ListView<FXPropertyType<? extends IPropertyType>>, ListCell<FXPropertyType<? extends IPropertyType>>>() {
		@Override
		public ListCell<FXPropertyType<? extends IPropertyType>> call(final ListView<FXPropertyType<? extends IPropertyType>> view) {
			return new PropertyTypeSelectionCell();
		}
	};
	
	@Override
	public void updateItem(final FXPropertyType<? extends IPropertyType> type, final boolean empty) {
		super.updateItem(type, empty);
		
		if (empty)
			textProperty().unbind();
		else
			textProperty().bind(type.labelProperty());
	}
}
