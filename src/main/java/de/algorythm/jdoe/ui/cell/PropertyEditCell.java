package de.algorythm.jdoe.ui.cell;

import java.util.ArrayList;
import java.util.Collection;

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
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.IPropertyType;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.attributeTypes.AbstractAttributeType;

public class PropertyEditCell extends AbstractLabeledListCell<Property> implements ChangeListener<IPropertyType> {
	
	static public class Factory implements Callback<ListView<Property>, ListCell<Property>> {

		private final ObservableList<EntityType> types;
		
		public Factory(final ObservableList<EntityType> types) {
			this.types = types;
		}
		
		@Override
		public ListCell<Property> call(ListView<Property> view) {
			return new PropertyEditCell(types);
		}
	}
	
	private final ObservableList<EntityType> types;
	private VBox editor = new VBox();
	private Button deleteButton = new Button("delete");
	private ComboBox<IPropertyType> typeComboBox = new ComboBox<>();
	private CheckBox searchableCheckBox = new CheckBox("searchable");
	private CheckBox containmentCheckBox = new CheckBox("contained");
	private CheckBox collectionCheckBox = new CheckBox("collection");
	private Collection<Node> attributeEditElements = new ArrayList<>(4);
	private Collection<Node> referenceEditElements = new ArrayList<>(5);
	
	public PropertyEditCell(final ObservableList<EntityType> types) {
		super();
		
		this.types = types;
		
		updateTypeComboBox();
		
		types.addListener(new ListChangeListener<EntityType>() {
			@Override
			public void onChanged(
					ListChangeListener.Change<? extends EntityType> arg0) {
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
		collectionCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> valueContainer,
					Boolean oldValue, Boolean newValue) {
				object.setCollection(newValue);
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
		referenceEditElements.add(collectionCheckBox);
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
		ArrayList<IPropertyType> availablePropertyTypes = new ArrayList<>();
		
		availablePropertyTypes.addAll(AbstractAttributeType.ATTRIBUTE_TYPES);
		availablePropertyTypes.addAll(types);
		typeComboBox.getItems().setAll(availablePropertyTypes);
	}
	
	@Override
	public void updateItem(Property property, boolean empty) {
		super.updateItem(property, empty);
		
		if (!empty) {
			typeComboBox.setValue(property.getType());
			searchableCheckBox.setSelected(property.isSearchable());
			containmentCheckBox.setSelected(property.isContainment());
			collectionCheckBox.setSelected(property.isCollection());
		}
	}
	
	@Override
	protected void showLabel() {
		final String name = object.getLabel();
		final IPropertyType type = object.getType();
		
		StringBuilder label = new StringBuilder();
		
		if (name != null)
			label.append(name);
		
		if (type != null)
			label.append(" (").append(object.getType().toString()).append(")");
		
		setText(label.toString());
	}
	
	@Override
	protected void showEditor() {
		Collection<Node> editElements = object.getType() instanceof EntityType
				? referenceEditElements : attributeEditElements;
		editor.getChildren().setAll(editElements);
		setGraphic(editor);
	}

	@Override
	public void changed(ObservableValue<? extends IPropertyType> valueContainer,
			IPropertyType oldType, IPropertyType newType) {
		if (newType != null) {
			object.setType(newType);
			showEditor();
		}
	}
}
