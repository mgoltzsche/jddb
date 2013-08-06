package de.algorythm.jdoe.ui.jfx.cell;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import de.algorythm.jdoe.model.entity.IEntity;

public class EntityCellValueFactory implements Callback<CellDataFeatures<IEntity, String>, ObservableValue<String>> {
	
	@Override
	public ObservableValue<String> call(
			final CellDataFeatures<IEntity, String> features) {
		final IEntity entity = features.getValue();
		
		return new SimpleStringProperty(entity.toString());
	}
}