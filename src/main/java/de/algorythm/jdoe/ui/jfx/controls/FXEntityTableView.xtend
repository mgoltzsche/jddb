package de.algorythm.jdoe.ui.jfx.controls

import de.algorythm.jdoe.bundle.Bundle
import de.algorythm.jdoe.model.meta.EntityType
import de.algorythm.jdoe.model.meta.EntityTypeWildcard
import de.algorythm.jdoe.model.meta.Property
import de.algorythm.jdoe.ui.jfx.cell.EntityCellValueFactory
import de.algorythm.jdoe.ui.jfx.cell.EntityRow
import de.algorythm.jdoe.ui.jfx.cell.EntityTypeCellValueFactory
import de.algorythm.jdoe.ui.jfx.cell.PropertyValueCell
import de.algorythm.jdoe.ui.jfx.comparator.StringComparator
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import java.util.LinkedList
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1

class FXEntityTableView extends TableView<FXEntity> {
	
	val bundle = Bundle.instance
	val entityTypeProperty = new SimpleObjectProperty<EntityType>()
	var Procedure1<FXEntity> onAction = []
	
	new() {
		super()
		cache = false
		setRowFactory(new EntityRow.Factory [
			onAction.apply(it)
		])
		entityTypeProperty.addListener [c,o,it|
			updateTableColumns
		]
	}
	
	def setOnAction(Procedure1<FXEntity> onAction) {
		this.onAction = onAction
	}
	
	def getEntityTypeProperty() {
		entityTypeProperty
	}
	
	def getEntityType() {
		entityTypeProperty.value
	}
	
	def setEntityType(EntityType entityType) {
		entityTypeProperty.value = entityType
	}
	
	def private updateTableColumns(EntityType entityType) {
		val columns = new LinkedList<TableColumn<FXEntity, ?>>
		
		if (entityType == EntityTypeWildcard.INSTANCE) {
			val typeColumn = new TableColumn<FXEntity, String>(bundle.type)
			val labelColumn = new TableColumn<FXEntity, String>(bundle.entity)
			
			typeColumn.cellValueFactory = EntityTypeCellValueFactory.INSTANCE
			labelColumn.cellValueFactory = EntityCellValueFactory.INSTANCE
			
			labelColumn.comparator = StringComparator.INSTANCE
			typeColumn.comparator = StringComparator.INSTANCE
			
			columns += typeColumn
			columns += labelColumn
		} else {
			var i = 0
			
			for (Property property : entityType.properties) {
				columns += createTableColumn(property.label, i)
				i = i + 1
			}
		}
		
		this.columns.all = columns
	}
	
	def private TableColumn<FXEntity,IFXPropertyValue<?>> createTableColumn(String label, int i) {
		val column = new TableColumn<FXEntity,IFXPropertyValue<?>>(label)
		column.cellFactory = new PropertyValueCell.Factory
		column.cellValueFactory = [
			val ObservableValue<IFXPropertyValue<?>> cell = new ReadOnlyObjectWrapper(value.getValue(i))
			cell
		]
		column
	}
}