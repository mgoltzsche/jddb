package de.algorythm.jddb.ui.jfx.model.meta.transform

import de.algorythm.jddb.ui.jfx.model.meta.FXEntityType
import de.algorythm.jddb.model.meta.MEntityType
import de.algorythm.jddb.model.meta.MProperty
import de.algorythm.jddb.ui.jfx.model.meta.FXProperty
import de.algorythm.jddb.ui.jfx.model.meta.IFXPropertyType
import de.algorythm.jddb.model.meta.IPropertyType
import de.algorythm.jddb.model.meta.propertyTypes.CollectionType
import de.algorythm.jddb.ui.jfx.model.meta.FXCollectionType
import de.algorythm.jddb.ui.jfx.model.meta.FXAttributeType
import de.algorythm.jddb.model.meta.propertyTypes.AbstractAttributeType

class ModelTransformation extends AbstractModelTransformation<MEntityType, FXEntityType> {
	
	override protected transform(MEntityType type) {
		val fxType = new FXEntityType
		fxType.label = type.label
		fxType.embedded = type.embedded
		
		transformLater[|
			for (MProperty property : type.getProperties()) {
				val fxProperty = new FXProperty
				
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
			CollectionType:           new FXCollectionType(itemType.transformed)
			AbstractAttributeType<?>: new FXAttributeType(type)
			default:                 throw new UnsupportedOperationException('''Unsupported transformation of type «type.getClass.name» («type.label»)''')
		}
	}
}