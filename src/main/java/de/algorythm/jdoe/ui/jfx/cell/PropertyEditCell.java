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
import de.algorythm.jdoe.bundle.Bundle;
import de.algorythm.jdoe.model.meta.IPropertyType;
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType;
import de.algorythm.jdoe.ui.jfx.model.meta.FXAttributeType;
import de.algorythm.jdoe.ui.jfx.model.meta.FXCollectionType;
import de.algorythm.jdoe.ui.jfx.model.meta.FXEntityType;
import de.algorythm.jdoe.ui.jfx.model.meta.FXProperty;
import de.algorythm.jdoe.ui.jfx.model.meta.IFXPropertyType;

public class PropertyEditCell extends AbstractLabeledListCell<FXProperty> {
	
	static private final Bundle bundle = Bundle.getInstance();
	
	static public final class Factory implements Callback<ListView<FXProperty>, ListCell<FXProperty>> {

		private final ObservableList<FXEntityType> types;
		
		public Factory(final ObservableList<FXEntityType> types) {
			this.types = types;
		}
		
		@Override
		public ListCell<FXProperty> call(ListView<FXProperty> view) {
			return new PropertyEditCell(types);
		}
	}
	
	static private final LinkedList<IFXPropertyType> FX_ATTRIBUTE_TYPES = new LinkedList<>();
	
	static {
		for (IPropertyType<?> attributeType : AbstractAttributeType.ATTRIBUTE_TYPES)
			FX_ATTRIBUTE_TYPES.add(new FXAttributeType(attributeType));
	}
	
	private final ObservableList<FXEntityType> types;
	private VBox editor = new VBox();
	private Button deleteButton = new Button(bundle.delete);
	private ComboBox<IFXPropertyType> typeComboBox = new ComboBox<>();
	private CheckBox searchableCheckBox = new CheckBox(bundle.searchable);
	private CheckBox containmentCheckBox = new CheckBox(bundle.contained);
	private Collection<Node> attributeEditElements = new ArrayList<>(4);
	private Collection<Node> referenceEditElements = new ArrayList<>(4);
	
	private final ChangeListener<IFXPropertyType> typeChangeListener = new ChangeListener<IFXPropertyType>() {
		@Override
		public void changed(ObservableValue<? extends IFXPropertyType> c,
				IFXPropertyType o, IFXPropertyType n) {
			showEditor();
		}
	};
	
	public PropertyEditCell(final ObservableList<FXEntityType> types) {
		super();
		
		this.types = types;
		
		typeComboBox.setCellFactory(PropertyTypeSelectionCell.FACTORY);
		typeComboBox.setButtonCell(new PropertyTypeSelectionCell());
		
		updateTypeComboBox();
		
		types.addListener(new ListChangeListener<FXEntityType>() {
			@Override
			public void onChanged(
					ListChangeListener.Change<? extends FXEntityType> evt) {
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
		typeComboBox.getSelectionModel().selectedItemProperty().addListener(typeChangeListener);
	}
	
	@Override
	public void cancelEdit() {
		super.cancelEdit();
		typeComboBox.getSelectionModel().selectedItemProperty().addListener(typeChangeListener);
	}
	
	private void updateTypeComboBox() {
		final int size = FX_ATTRIBUTE_TYPES.size() + types.size();
		final ArrayList<IFXPropertyType> availablePropertyTypes = new ArrayList<>(size);
		
		availablePropertyTypes.addAll(FX_ATTRIBUTE_TYPES);
		
		for (FXEntityType fxType : types) {
			availablePropertyTypes.add(fxType);
			availablePropertyTypes.add(new FXCollectionType(fxType));
		}
		
		typeComboBox.getItems().setAll(availablePropertyTypes);
	}
	
	@Override
	public void updateItem(FXProperty property, boolean empty) {
		super.updateItem(property, empty);
		
		if (!empty) {
			typeComboBox.setValue(property.getType());
			searchableCheckBox.setSelected(property.isSearchable());
			containmentCheckBox.setSelected(property.isContainment());
		}
	}
	
	@Override
	protected void showLabel() {
		final String name = object.getLabel();
		final IFXPropertyType type = object.getType();
		
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
}
