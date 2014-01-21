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

class FXModelTransformation extends AbstractModelTransformation<FXEntityType, MEntityType> {
	
	override protected transform(FXEntityType fxType) {
		val type = new MEntityType
		type.label = fxType.label
		type.embedded = fxType.embedded
		
		transformLater[|
			for (FXProperty fxProperty : fxType.getProperties()) {
				val property = new MProperty
				
				property.label = fxProperty.label
				property.type = fxProperty.type.transform
				property.searchable = fxProperty.searchable
				property.containment = fxProperty.containment
				
				type.properties += property
			}
		]
		
		type
	}
	
	def private IPropertyType<?> transform(IFXPropertyType fxType) {
		switch it : fxType {
			FXEntityType:     transformed
			FXCollectionType: new CollectionType(itemType.transformed)
			FXAttributeType:  type
			default:         throw new UnsupportedOperationException('''Unsupported transformation of type «fxType.getClass.name» («fxType.label»)''')
		}
	}
}