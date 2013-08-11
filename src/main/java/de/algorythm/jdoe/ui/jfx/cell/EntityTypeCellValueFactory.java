package de.algorythm.jdoe.ui.jfx.cell;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;

public class EntityTypeCellValueFactory implements Callback<CellDataFeatures<FXEntity, String>, ObservableValue<String>> {
	
	static public final EntityTypeCellValueFactory INSTANCE = new EntityTypeCellValueFactory();
	
	private EntityTypeCellValueFactory() {}
	
	@Override
	public ObservableValue<String> call(
			final CellDataFeatures<FXEntity, String> features) {
		return new SimpleStringProperty(features.getValue().getType().getLabel());
	}
}