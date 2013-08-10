package de.algorythm.jdoe.ui.jfx.cell;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXPropertyValue;

public class PropertyCellValueFactory implements Callback<CellDataFeatures<FXEntity, String>, ObservableValue<String>> {

	private final int index;
	
	public PropertyCellValueFactory(final int index) {
		this.index = index;
	}
	
	@Override
	public ObservableValue<String> call(
			final CellDataFeatures<FXEntity, String> features) {
		final FXEntity entity = features.getValue();
		final FXPropertyValue value = entity.getValue(index);
		
		return value.getValue();
	}
}