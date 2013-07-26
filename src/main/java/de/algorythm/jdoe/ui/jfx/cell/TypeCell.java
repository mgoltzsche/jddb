package de.algorythm.jdoe.ui.jfx.cell;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import de.algorythm.jdoe.ui.jfx.model.FXType;

public class TypeCell extends AbstractLabeledListCell<FXType> {
	
	static public class Factory implements Callback<ListView<FXType>, ListCell<FXType>> {
	
		@Override
		public ListCell<FXType> call(ListView<FXType> view) {
			return new TypeCell();
		}
	}
}
