package de.algorythm.jdoe.ui.jfx.cell;

import de.algorythm.jdoe.model.meta.IPropertyType
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType
import de.algorythm.jdoe.ui.jfx.model.meta.FXAttributeType
import de.algorythm.jdoe.ui.jfx.model.meta.FXCollectionType
import de.algorythm.jdoe.ui.jfx.model.meta.FXEntityType
import de.algorythm.jdoe.ui.jfx.model.meta.FXProperty
import de.algorythm.jdoe.ui.jfx.model.meta.IFXPropertyType
import java.util.ArrayList
import java.util.LinkedList
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.layout.HBox
import javafx.geometry.Pos
import javafx.scene.layout.Region
import javafx.scene.layout.Priority

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
	val searchableCheckBox = new CheckBox(bundle.searchable)
	val containmentCheckBox = new CheckBox(bundle.contained)
	val spacer = new Region
	val attributeEditElements = newLinkedList(typeComboBox, searchableCheckBox, spacer, deleteButton)
	val referenceEditElements = newLinkedList(typeComboBox, containmentCheckBox, spacer, deleteButton)
	
	val ChangeListener<IFXPropertyType> typeChangeListener = [c,o,type|
		editObject.type = type
		
		if (editObject != null)
			updateEditorNodes
	]
	
	new(ObservableList<FXEntityType> types) {
		super()
		
		this.types = types
		
		typeComboBox.cellFactory = PropertyTypeSelectionCell.FACTORY
		typeComboBox.buttonCell = new PropertyTypeSelectionCell
		
		updateTypeComboBox
		
		HBox.setHgrow(spacer, Priority.ALWAYS)
		hBox.alignment = Pos.BASELINE_CENTER
		editor.children.all = newLinkedList(labelInput, hBox)
		
		types.addListener [
			updateTypeComboBox
		]
	}
	
	override startEdit() {
		super.startEdit
		val typeSelection = typeComboBox.selectionModel
		typeSelection.select(editObject.type)
		typeSelection.selectedItemProperty.addListener(typeChangeListener)
	}
	
	override cancelEdit() {
		super.cancelEdit
		typeComboBox.selectionModel.selectedItemProperty.removeListener(typeChangeListener)
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
	
	/*@Override
	protected void showLabel() {
		final String name = object.getLabel();
		final IFXPropertyType type = object.getType();
		
		StringBuilder label = new StringBuilder();
		
		if (name != null)
			label.append(name);
		
		if (type != null && !type.isUserDefined())
			label.append(" (").append(object.getType().toString()).append(")");
		
		setText(label.toString());
	}*/
	
	override updateEditorNodes() {
		hBox.children.all = if (editObject.type.userDefined)
			referenceEditElements
		else
			attributeEditElements
	}
	
	override doWithProperties(AbstractLabeledListCell.BindingHandler<FXProperty> it) {
		super.doWithProperties(it)

		doWithProperty(searchableCheckBox.selectedProperty) [
			editObject.searchableProperty
		]
		doWithProperty(containmentCheckBox.selectedProperty) [
			editObject.containmentProperty
		]
	}
}
