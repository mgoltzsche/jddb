package de.algorythm.jdoe.ui.jfx.model.meta;

import de.algorythm.jdoe.model.meta.propertyTypes.TString;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class FXProperty extends FXAbstractLabeledElement {

	private final Property<String> labelWithTypeProperty = new SimpleStringProperty();
	private final ObjectProperty<IFXPropertyType> type = new SimpleObjectProperty<IFXPropertyType>(new FXAttributeType(new TString()));
	private final BooleanProperty searchable = new SimpleBooleanProperty(true);
	private final BooleanProperty containment = new SimpleBooleanProperty();
	
	private final ChangeListener<String> typeLabelListener = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> c,
				String o, String n) {
			updateLabelWithTypeProperty();
		}
	};
	
	public FXProperty() {
		label.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> c,
					String o, String n) {
				updateLabelWithTypeProperty();
			}
		});
		type.addListener(new ChangeListener<IFXPropertyType>() {
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
		labelWithTypeProperty.setValue(label.get() + " (" + type.get().getLabel() + ")");
	}
	
	public ReadOnlyProperty<String> labelWithTypeProperty() {
		return labelWithTypeProperty;
	}
	
	public Property<IFXPropertyType> typeProperty() {
		return type;
	}
	
	public IFXPropertyType getType() {
		return type.get();
	}
	
	public void setType(final IFXPropertyType type) {
		this.type.set(type);
	}
	
	public Property<Boolean> searchableProperty() {
		return searchable;
	}
	
	public Boolean isSearchable() {
		return searchable.get();
	}
	
	public void setSearchable(final Boolean searchable) {
		this.searchable.set(searchable);
	}
	
	public Property<Boolean> containmentProperty() {
		return containment;
	}
	
	public Boolean isContainment() {
		return containment.get();
	}
	
	public void setContainment(final Boolean containment) {
		this.containment.set(containment);
	}
}