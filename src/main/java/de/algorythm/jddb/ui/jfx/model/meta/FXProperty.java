package de.algorythm.jddb.ui.jfx.model.meta;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.algorythm.jddb.model.meta.propertyTypes.TString;

public class FXProperty extends FXAbstractLabeledChangable {

	private final Property<String> labelWithTypeProperty = new SimpleStringProperty();
	private final ObjectProperty<IFXPropertyType> typeProperty = new SimpleObjectProperty<IFXPropertyType>(new FXAttributeType(TString.getInstance()));
	private final BooleanProperty searchableProperty = new SimpleBooleanProperty(true);
	private final BooleanProperty containmentProperty = new SimpleBooleanProperty();
	
	private final ChangeListener<String> typeLabelListener = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> c,
				String o, String n) {
			updateLabelWithTypeProperty();
		}
	};
	
	public FXProperty(final InvalidationListener invalidationListener) {
		super(invalidationListener);
		
		updateLabelWithTypeProperty();
		typeProperty.addListener(invalidationListener);
		labelProperty.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> c,
					String o, String n) {
				updateLabelWithTypeProperty();
			}
		});
		typeProperty.addListener(new ChangeListener<IFXPropertyType>() {
			@Override
			public void changed(
					ObservableValue<? extends IFXPropertyType> c,
					IFXPropertyType o, IFXPropertyType n) {
				if (o != null)
					o.labelProperty().removeListener(typeLabelListener);
				
				n.labelProperty().addListener(typeLabelListener);
				updateLabelWithTypeProperty();
			}
		});
	}
	
	private void updateLabelWithTypeProperty() {
		labelWithTypeProperty.setValue(labelProperty.get() + " (" + typeProperty.get().getLabel() + ")");
	}
	
	public ReadOnlyProperty<String> labelWithTypeProperty() {
		return labelWithTypeProperty;
	}
	
	public Property<IFXPropertyType> typeProperty() {
		return typeProperty;
	}
	
	public IFXPropertyType getType() {
		return typeProperty.get();
	}
	
	public void setType(final IFXPropertyType type) {
		typeProperty.set(type);
	}
	
	public Property<Boolean> searchableProperty() {
		return searchableProperty;
	}
	
	public Boolean isSearchable() {
		return searchableProperty.get();
	}
	
	public void setSearchable(final Boolean searchable) {
		searchableProperty.set(searchable);
	}
	
	public Property<Boolean> containmentProperty() {
		return containmentProperty;
	}
	
	public Boolean isContainment() {
		return containmentProperty.get();
	}
	
	public void setContainment(final Boolean containment) {
		containmentProperty.set(containment);
	}
}