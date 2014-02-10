package de.algorythm.jddb.ui.jfx.model.meta.transform

import de.algorythm.jddb.model.meta.IPropertyType
import de.algorythm.jddb.model.meta.MEntityType
import de.algorythm.jddb.model.meta.MProperty
import de.algorythm.jddb.model.meta.propertyTypes.AbstractAttributeType
import de.algorythm.jddb.model.meta.propertyTypes.CollectionType
import de.algorythm.jddb.ui.jfx.model.meta.FXAttributeType
import de.algorythm.jddb.ui.jfx.model.meta.FXCollectionType
import de.algorythm.jddb.ui.jfx.model.meta.FXEntityType
import de.algorythm.jddb.ui.jfx.model.meta.FXProperty
import javafx.beans.InvalidationListener
import de.algorythm.jddb.ui.jfx.model.meta.IFXPropertyType

class ModelTransformation extends AbstractModelTransformation<MEntityType, FXEntityType> {
	
	val InvalidationListener invalidationListener
	
	new(InvalidationListener invalidationListener) {
		this.invalidationListener = invalidationListener
	}
	
	override protected transform(MEntityType type) {
		val fxType = new FXEntityType(invalidationListener)
		fxType.label = type.label
		fxType.embedded = type.embedded
		
		transformLater[|
			for (MProperty property : type.getProperties()) {
				val fxProperty = new FXProperty(invalidationListener)
				
				fxProperty.label = property.label
				fxProperty.type = property.type.transform
				fxProperty.searchable = property.searchable
				fxProperty.containment = property.containment
				
				fxType.properties += fxProperty
			}
		]
		
		fxType
	}
	
	def private IFXPropertyType transform(IPropertyType<?> type) {
		switch it : type {
			MEntityType:              transformed
			CollectionType:           new FXCollectionType(getItemType.transformed)
			AbstractAttributeType<?>: new FXAttributeType(type)
			default:                 throw new UnsupportedOperationException('''Unsupported transformation of type «type.getClass.name» («type.label»)''')
		}
	}
}