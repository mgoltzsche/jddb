package de.algorythm.jdoe.ui.cell;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class PropertyCellValueFactory implements Callback<CellDataFeatures<IEntity, String>, ObservableValue<String>> {

	private int index;
	
	public PropertyCellValueFactory(final int index) {
		this.index = index;
	}
	
	@Override
	public ObservableValue<String> call(
			final CellDataFeatures<IEntity, String> features) {
		final IEntity entity = features.getValue();
		final IPropertyValue value = entity.getValue(index);
		
		return new SimpleStringProperty(value.toString());
	}

}