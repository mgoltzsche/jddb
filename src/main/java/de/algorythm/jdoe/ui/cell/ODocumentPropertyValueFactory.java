package de.algorythm.jdoe.ui.cell;

import com.orientechnologies.orient.core.record.impl.ODocument;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class ODocumentPropertyValueFactory implements Callback<CellDataFeatures<ODocument, String>, ObservableValue<String>> {

	private final String fieldName;
	
	public ODocumentPropertyValueFactory(final String fieldName) {
		this.fieldName = fieldName;
	}
	
	@Override
	public ObservableValue<String> call(CellDataFeatures<ODocument, String> features) {
		final String value = features.getValue().field(fieldName);
		
		return new SimpleStringProperty(value);
	}

}
