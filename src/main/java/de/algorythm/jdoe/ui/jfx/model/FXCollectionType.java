package de.algorythm.jdoe.ui.jfx.model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.algorythm.jdoe.model.meta.propertyTypes.CollectionType;

public class FXCollectionType extends FXPropertyType<CollectionType> {

	public FXCollectionType(final FXType fxType, final CollectionType businessModel) {
		super(businessModel);
		labelProperty.setValue(businessModel.getLabel());
		
		fxType.labelProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> valueContainer,
					String oldValue, String newValue) {
				labelProperty.setValue(businessModel.getLabel());
			}
		});
	}
}
