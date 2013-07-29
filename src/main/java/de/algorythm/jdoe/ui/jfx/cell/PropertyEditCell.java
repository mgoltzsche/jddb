package de.algorythm.jdoe.ui.jfx.cell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import de.algorythm.jdoe.model.meta.IPropertyType;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jdoe.model.meta.propertyTypes.CollectionType;
import de.algorythm.jdoe.ui.jfx.model.FXCollectionType;
import de.algorythm.jdoe.ui.jfx.model.FXPropertyType;
import de.algorythm.jdoe.ui.jfx.model.FXType;

public class PropertyEditCell extends AbstractLabeledListCell<Property> implements ChangeListener<FXPropertyType<? extends IPropertyType>> {
	
	static public class Factory implements Callback<ListView<Property>, ListCell<Property>> {

		private final ObservableList<FXType> types;
		
		public Factory(final ObservableList<FXType> types) {
			this.types = types;
		}
		
		@Override
		public ListCell<Property> call(ListView<Property> view) {
			return new PropertyEditCell(types);
		}
	}
	
	static private final LinkedList<FXPropertyType<? extends IPropertyType>> FX_ATTRIBUTE_TYPES = new LinkedList<>();
	
	static {
		for (IPropertyType attributeType : AbstractAttributeType.ATTRIBUTE_TYPES)
			FX_ATTRIBUTE_TYPES.add(new FXPropertyType<IPropertyType>(attributeType));
	}
	
	private final ObservableList<FXType> types;
	private VBox editor = new VBox();
	private Button deleteButton = new Button("delete");
	private ComboBox<FXPropertyType<? extends IPropertyType>> typeComboBox = new ComboBox<>();
	private CheckBox searchableCheckBox = new CheckBox("searchable");
	private CheckBox containmentCheckBox = new CheckBox("contained");
	private Collection<Node> attributeEditElements = new ArrayList<>(4);
	private Collection<Node> referenceEditElements = new ArrayList<>(4);
	
	public PropertyEditCell(final ObservableList<FXType> types) {
		super();
		
		this.types = types;
		
		typeComboBox.setCellFactory(PropertyTypeSelectionCell.FACTORY);
		typeComboBox.setButtonCell(new PropertyTypeSelectionCell());
		
		updateTypeComboBox();
		
		types.addListener(new ListChangeListener<FXType>() {
			@Override
			public void onChanged(
					ListChangeListener.Change<? extends FXType> evt) {
				updateTypeComboBox();
			}
		});
		containmentCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> valueContainer,
					Boolean oldValue, Boolean newValue) {
				object.setContainment(newValue);
			}
		});
		searchableCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> valueContainer,
					Boolean oldValue, Boolean newValue) {
				object.setSearchable(newValue);
			}
		});
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent evt) {
				getListView().itemsProperty().getValue().remove(object);
			}
		});
		
		attributeEditElements.add(labelInput);
		attributeEditElements.add(typeComboBox);
		attributeEditElements.add(searchableCheckBox);
		attributeEditElements.add(deleteButton);
		
		referenceEditElements.add(labelInput);
		referenceEditElements.add(typeComboBox);
		referenceEditElements.add(containmentCheckBox);
		referenceEditElements.add(deleteButton);
	}
	
	@Override
	public void startEdit() {
		super.startEdit();
		typeComboBox.getSelectionModel().selectedItemProperty().addListener(this);
	}
	
	@Override
	public void cancelEdit() {
		super.cancelEdit();
		typeComboBox.getSelectionModel().selectedItemProperty().removeListener(this);
	}
	
	private void updateTypeComboBox() {
		final int size = FX_ATTRIBUTE_TYPES.size() + types.size();
		final ArrayList<FXPropertyType<? extends IPropertyType>> availablePropertyTypes = new ArrayList<>(size);
		
		availablePropertyTypes.addAll(FX_ATTRIBUTE_TYPES);
		
		for (FXType fxType : types) {
			availablePropertyTypes.add(fxType);
			availablePropertyTypes.add(new FXCollectionType(fxType, new CollectionType(fxType.getBusinessModel())));
		}
		
		typeComboBox.getItems().setAll(availablePropertyTypes);
	}
	
	@Override
	public void updateItem(Property property, boolean empty) {
		super.updateItem(property, empty);
		
		if (!empty) {
			typeComboBox.setValue(getFxPropertyType(property.getType()));
			searchableCheckBox.setSelected(property.isSearchable());
			containmentCheckBox.setSelected(property.isContainment());
		}
	}
	
	private FXPropertyType<? extends IPropertyType> getFxPropertyType(final IPropertyType propertyType) {
		for (FXPropertyType<? extends IPropertyType> fxType : typeComboBox.getItems())
			if (fxType.getBusinessModel().equals(propertyType))
				return fxType;
		return null;
	}
	
	@Override
	protected void showLabel() {
		final String name = object.getLabel();
		final IPropertyType type = object.getType();
		
		StringBuilder label = new StringBuilder();
		
		if (name != null)
			label.append(name);
		
		if (type != null && !type.isUserDefined())
			label.append(" (").append(object.getType().toString()).append(")");
		
		setText(label.toString());
	}
	
	@Override
	protected void showEditor() {
		Collection<Node> editElements = object.getType().isUserDefined()
				? referenceEditElements : attributeEditElements;
		editor.getChildren().setAll(editElements);
		setGraphic(editor);
	}

	@Override
	public void changed(ObservableValue<? extends FXPropertyType<? extends IPropertyType>> valueContainer,
			FXPropertyType<? extends IPropertyType> oldValue, FXPropertyType<? extends IPropertyType> newValue) {
		if (newValue != null) {
			object.setType(newValue.getBusinessModel());
			showEditor();
		}
	}
}
