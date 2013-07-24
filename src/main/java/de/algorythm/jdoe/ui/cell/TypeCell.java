package de.algorythm.jdoe.ui.cell;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import de.algorythm.jdoe.model.meta.EntityType;

public class TypeCell extends AbstractLabeledListCell<EntityType> {
	
	static public class Factory implements Callback<ListView<EntityType>, ListCell<EntityType>> {

		@Override
		public ListCell<EntityType> call(ListView<EntityType> view) {
			return new TypeCell();
		}
	}
}
