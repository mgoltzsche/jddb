package de.algorythm.jdoe.controller;

import java.io.IOException;
import java.util.Collection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.Schema;
import de.algorythm.jdoe.ui.cell.PropertyEditCell;
import de.algorythm.jdoe.ui.cell.TypeCell;

public class TypeEditorController implements IController {

	static private final Logger log = LoggerFactory
			.getLogger(TypeEditorController.class);
	
	@FXML private ListView<EntityType> typeList;
	@FXML private ListView<Property> propertyList;
	@FXML private Button btnAddProperty;
	@Inject private IDAO dao;
	private ObservableList<EntityType> types;
	private ObservableList<Property> properties;
	private EntityType selectedType;
	private Schema schema;
	
	private ListChangeListener<Property> propertyListChangeListener = new ListChangeListener<Property>() {
		@Override
		public void onChanged(ListChangeListener.Change<? extends Property> change) {
			final Collection<Property> typeProperties = selectedType.getProperties();
			typeProperties.clear();
			typeProperties.addAll(properties);
		}
	};
	
	@Override
	public void init() {
		schema = dao.getSchema();
		
		// setup type list
		types = typeList.getItems();
		types.setAll(schema.getTypes());
		
		types.addListener(new ListChangeListener<EntityType>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends EntityType> change) {
				final Collection<EntityType> schemaTypes = schema.getTypes();
				
				schemaTypes.clear();
				schemaTypes.addAll(types);
			}
		}); 
		
		typeList.setCellFactory(new TypeCell.Factory());
		typeList.setEditable(true);
		typeList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EntityType>() {
			@Override
			public void changed(ObservableValue<? extends EntityType> valueContainer,
					EntityType oldType, EntityType newType) {
				selectType(newType);
			}
		});
		
		// setup property list
		propertyList.setEditable(true);
		propertyList.setCellFactory(new PropertyEditCell.Factory(types));
		properties = propertyList.getItems();
	}
	
	public void save() {
		try {
			dao.setSchema(schema);
		} catch (IOException e) {
			log.error("Cannot save schema", e);
		}
	}
	
	public void addType() {
		types.add(new EntityType());
	}
	
	public void addProperty() {
		if (selectedType != null)
			properties.add(new Property());
	}
	
	private void selectType(final EntityType type) {
		properties.removeListener(propertyListChangeListener);
		
		selectedType = type;
		
		if (selectedType == null) {
			properties.clear();
		} else {
			properties.setAll(selectedType.getProperties());
			properties.addListener(propertyListChangeListener);
			btnAddProperty.setDisable(false);		
		}
	}
}
