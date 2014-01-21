package de.algorythm.jddb.ui.jfx.cell;

import de.algorythm.jddb.ui.jfx.model.meta.FXEntityType;
import de.algorythm.jddb.ui.jfx.model.meta.FXProperty;
import de.algorythm.jddb.ui.jfx.cell.PropertyEditCell;
import de.algorythm.jddb.ui.jfx.cell.TypeCell;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class MetamodelElementCellFactories {

	static public class TypeCellFactory implements Callback<ListView<FXEntityType>, ListCell<FXEntityType>> {
		@Override
		public ListCell<FXEntityType> call(ListView<FXEntityType> view) {
			return new TypeCell();
		}
	}
	
	static public final class PropertyCellFactory implements Callback<ListView<FXProperty>, ListCell<FXProperty>> {

		private final ObservableList<FXEntityType> types;
		
		public PropertyCellFactory(final ObservableList<FXEntityType> types) {
			this.types = types;
		}
		
		@Override
		public ListCell<FXProperty> call(ListView<FXProperty> view) {
			return new PropertyEditCell(types);
		}
	}
}
