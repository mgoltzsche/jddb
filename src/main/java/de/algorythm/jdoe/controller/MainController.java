package de.algorythm.jdoe.controller;

import java.io.IOException;
import java.util.LinkedList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.inject.Inject;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.jdoe.JavaDbObjectEditorFacade;
import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.Schema;
import de.algorythm.jdoe.ui.cell.EntityRow;
import de.algorythm.jdoe.ui.cell.PropertyCellValueFactory;

public class MainController implements IController {

	static private final Logger log = LoggerFactory
			.getLogger(MainController.class);
	
	@Inject private IDAO dao;
	@Inject private JavaDbObjectEditorFacade facade;
	@FXML private ComboBox<EntityType> typeComboBox;
	@FXML private TabPane tabs;
	@FXML private TableView<IEntity> entityList;
	private Schema schema;
	private EntityType selectedType;
	
	@Override
	public void init() {
		schema = dao.getSchema();
		
		entityList.setRowFactory(new EntityRow.Factory(new Procedure1<IEntity>() {
			@Override
			public void apply(final IEntity entity) {
				try {
					facade.showEntityEditor(entity, tabs);
				} catch (IOException e) {
					log.error("Cannot open entity editor", e);
				}
			}
		}));
		
		typeComboBox.valueProperty().addListener(new ChangeListener<EntityType>() {
			@Override
			public void changed(ObservableValue<? extends EntityType> valueContainer,
					EntityType oldType, EntityType newType) {
				setSelectedType(newType);
			}
		});
		
		setupTypeSelection();
	}
	
	private void setSelectedType(final EntityType type) {
		selectedType = type == null ? EntityType.DEFAULT : type;
		updateTableColumns();
		tabs.getSelectionModel().select(0);
	}
	
	private void setupTypeSelection() {
		typeComboBox.getItems().setAll(schema.getTypes());
		typeComboBox.getSelectionModel().selectFirst();
	}
	
	private void updateTableColumns() {
		final LinkedList<TableColumn<IEntity, ?>> columns = new LinkedList<>();
		
		for (Property property : selectedType.getProperties()) {
			final TableColumn<IEntity, String> column = new TableColumn<IEntity, String>(property.getLabel());
			
			column.setCellValueFactory(new PropertyCellValueFactory(property.getIndex()));
			columns.add(column);
		}
		
		updateTableData();
		entityList.getColumns().setAll(columns);
	}
	
	private void updateTableData() {
		entityList.getItems().setAll(dao.list(selectedType));
	}
	
	public void openDatabase() {
		// TODO
	}
	
	public void showTypeEditor() throws IOException {
		facade.showTypeEditor();
	}
	
	public void createEntity() {
		dao.create(selectedType);
		updateTableData();
	}
}
