package de.algorythm.jdoe.ui.jfx.cell;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;

public class EntityCellValueFactory implements Callback<CellDataFeatures<FXEntity, String>, ObservableValue<String>> {
	
	static public final EntityCellValueFactory INSTANCE = new EntityCellValueFactory();
	
	private EntityCellValueFactory() {}
	
	@Override
	public ObservableValue<String> call(
			final CellDataFeatures<FXEntity, String> features) {
		return features.getValue().labelProperty();
	}
}