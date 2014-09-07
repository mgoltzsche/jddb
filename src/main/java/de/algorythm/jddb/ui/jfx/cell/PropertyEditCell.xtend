package de.algorythm.jddb.ui.jfx.cell;

import de.algorythm.jddb.model.meta.IPropertyType
import de.algorythm.jddb.model.meta.propertyTypes.AbstractAttributeType
import de.algorythm.jddb.ui.jfx.model.meta.FXAttributeType
import de.algorythm.jddb.ui.jfx.model.meta.FXCollectionType
import de.algorythm.jddb.ui.jfx.model.meta.FXEntityType
import de.algorythm.jddb.ui.jfx.model.meta.FXProperty
import de.algorythm.jddb.ui.jfx.model.meta.IFXPropertyType
import java.util.ArrayList
import java.util.LinkedList
import javafx.beans.Observable
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region

public class PropertyEditCell extends AbstractLabeledListCell<FXProperty> {
	
	static val FX_ATTRIBUTE_TYPES = {
		val attrTypes = new LinkedList<IFXPropertyType>
		for (IPropertyType<?> attributeType : AbstractAttributeType.ATTRIBUTE_TYPES)
			attrTypes.add(new FXAttributeType(attributeType))
		attrTypes
	}
	
	val ObservableList<FXEntityType> types
	val hBox = new HBox(5)
	val typeComboBox = new ComboBox<IFXPropertyType>
	val checkBox = new CheckBox(bundle.searchable)
	val spacer = new Region
	val ChangeListener<IFXPropertyType> typeChangeListener = [c,o,n|
		editItem.type = n
		updateCheckBox
	]
	val ChangeListener<Boolean> checkBoxChangeListener = [c,o,n|
		if (editItem.type.userDefined)
			editItem.containment = n
		else
			editItem.searchable = n
	]
	
	new(ObservableList<FXEntityType> types) {
		super()
		
		this.types = types
		
		typeComboBox.cellFactory = PropertyTypeSelectionCell.FACTORY
		typeComboBox.buttonCell = new PropertyTypeSelectionCell
		
		updateTypeComboBox
		
		HBox.setHgrow(spacer, Priority.ALWAYS)
		hBox.alignment = Pos.BASELINE_CENTER
		val hBoxChildren = hBox.children
		
		hBoxChildren += typeComboBox
		hBoxChildren += checkBox
		hBoxChildren += spacer
		hBoxChildren += deleteButton
		
		editor.children.all = newLinkedList(labelInput, hBox)
		
		types.addListener [Observable o|
			updateTypeComboBox
		]
	}
	
	def void updateTypeComboBox() {
		val size = FX_ATTRIBUTE_TYPES.size() + types.size();
		val availablePropertyTypes = new ArrayList<IFXPropertyType>(size);
		
		availablePropertyTypes += FX_ATTRIBUTE_TYPES
		
		for (FXEntityType fxType : types) {
			availablePropertyTypes += fxType
			availablePropertyTypes += new FXCollectionType(fxType)
		}
		
		typeComboBox.items.all = availablePropertyTypes
	}
	
	override showLabel() {
		textProperty.bind(item.labelWithTypeProperty)
	}
	
	override doWithProperties(AbstractLabeledListCell.BindingHandler<FXProperty> it) {
		super.doWithProperties(it)

		updateCheckBox
		doWithProperty(typeComboBox.selectionModel, editItem.type, typeChangeListener)
		doWithProperty(checkBox.selectedProperty, checkBox.selected, checkBoxChangeListener)
	}
	
	def private updateCheckBox() {
		checkBox.disable = editItem.type.embedded
		
		if (editItem.type.userDefined) {
			if (editItem.type.embedded)
				editItem.containment = true
			
			checkBox.text = bundle.contained
			checkBox.selected = editItem.containment
		} else {
			checkBox.text = bundle.searchable
			checkBox.selected = editItem.searchable
		}
	}
}
