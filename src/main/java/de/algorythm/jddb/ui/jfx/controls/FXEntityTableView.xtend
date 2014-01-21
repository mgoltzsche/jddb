package de.algorythm.jddb.ui.jfx.controls

import de.algorythm.jddb.bundle.Bundle
import de.algorythm.jddb.ui.jfx.cell.EntityCellValueFactory
import de.algorythm.jddb.ui.jfx.cell.EntityRowFactory
import de.algorythm.jddb.ui.jfx.cell.EntityTypeCellValueFactory
import de.algorythm.jddb.ui.jfx.cell.PropertyValueCell
import de.algorythm.jddb.ui.jfx.comparator.StringComparator
import de.algorythm.jddb.ui.jfx.model.FXEntity
import de.algorythm.jddb.ui.jfx.model.propertyValue.IFXPropertyValue
import java.util.LinkedList
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2
import javafx.scene.Node
import de.algorythm.jddb.model.meta.MEntityType
import de.algorythm.jddb.model.meta.MEntityTypeWildcard
import de.algorythm.jddb.model.meta.MProperty

class FXEntityTableView extends TableView<FXEntity> {
	
	val bundle = Bundle.instance
	val entityTypeProperty = new SimpleObjectProperty<MEntityType>()
	var Procedure1<FXEntity> onRowClick = []
	var Procedure2<FXEntity, Node> onRowEnter = [entity,node|]
	
	new() {
		super()
		cache = false
		setRowFactory(new EntityRowFactory([
			onRowClick.apply(it)
		], [e,n|
			onRowEnter.apply(e,n) 
		]))
		entityTypeProperty.addListener [c,o,it|
			updateTableColumns
		]
	}
	
	def setOnRowClick(Procedure1<FXEntity> onRowClick) {
		this.onRowClick = onRowClick
	}
	
	def setOnRowEnter(Procedure2<FXEntity, Node> onRowEnter) {
		this.onRowEnter = onRowEnter
	}
	
	def getEntityTypeProperty() {
		entityTypeProperty
	}
	
	def getEntityType() {
		entityTypeProperty.value
	}
	
	def setEntityType(MEntityType entityType) {
		entityTypeProperty.value = entityType
	}
	
	def private updateTableColumns(MEntityType entityType) {
		val columns = new LinkedList<TableColumn<FXEntity, ?>>
		
		if (entityType == MEntityTypeWildcard.INSTANCE) {
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
			
			for (MProperty property : entityType.getProperties) {
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