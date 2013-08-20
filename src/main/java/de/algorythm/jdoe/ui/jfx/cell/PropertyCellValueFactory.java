package de.algorythm.jdoe.ui.jfx.cell;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;

public class PropertyCellValueFactory implements Callback<CellDataFeatures<FXEntity, String>, ObservableValue<String>> {

	private final int propertyIndex;
	
	public PropertyCellValueFactory(final int propertyIndex) {
		this.propertyIndex = propertyIndex;
	}
	
	@Override
	public ObservableValue<String> call(
			final CellDataFeatures<FXEntity, String> features) {
		final FXEntity entity = features.getValue();
		final IFXPropertyValue<?> value = entity.getValue(propertyIndex);
		
		return value.labelProperty();
	}
}