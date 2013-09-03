package de.algorythm.jdoe.ui.jfx.model.factory

import de.algorythm.jdoe.cache.ObjectCache
import de.algorythm.jdoe.model.dao.IModelFactory
import de.algorythm.jdoe.model.dao.IPropertyValueLoader
import de.algorythm.jdoe.model.entity.IPropertyValueFactory
import de.algorythm.jdoe.model.meta.EntityType
import de.algorythm.jdoe.model.meta.Property
import de.algorythm.jdoe.model.meta.propertyTypes.AbstractAttributeType
import de.algorythm.jdoe.ui.jfx.model.FXEntity
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociation
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociations
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAttribute
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue
import java.util.ArrayList
import javax.inject.Singleton
import javax.inject.Inject
import de.algorythm.jdoe.model.dao.IDAO
import javax.annotation.PostConstruct
import de.algorythm.jdoe.model.dao.IObserver
import de.algorythm.jdoe.model.dao.ModelChange

@Singleton
public class FXModelFactory implements IModelFactory<FXEntity, IFXPropertyValue<?>, FXEntityReference>, IPropertyValueFactory<IFXPropertyValue<?>, FXEntityReference>, IObserver<FXEntity, IFXPropertyValue<?>, FXEntityReference> {

	@Inject IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao
	val cachedEntities = new ObjectCache<FXEntity>
	
	@PostConstruct
	def init() {
		dao.addObserver(this)
	}
	
	override createNewEntity(EntityType type) {
		val entity = new FXEntity(type)
		val properties = type.properties
		val values = new ArrayList(properties.size)
		
		for (Property property : properties)
			values += property.createPropertyValue(this)
		
		entity.values = values
		
		entity
	}

	override createEntity(IPropertyValueLoader<FXEntityReference> loader) {
		createEntity(loader, true)
	}

	override createEntityReference(IPropertyValueLoader<FXEntityReference> loader) {
		createEntity(loader, false)
	}

	def private FXEntity createEntity(IPropertyValueLoader<FXEntityReference> loader, boolean withAssociations) {
		val id = loader.id
		val cachedEntity = cachedEntities.get(id)
		
		if (cachedEntity == null) {
			val type = loader.type
			val entity = new FXEntity(id, type, !withAssociations)
			val properties = type.properties
			val values = new ArrayList<IFXPropertyValue<?>>(properties.size)
			
			cachedEntities.put(id, entity)
			
			for (Property property : type.properties) {
				val propertyValue = property.createPropertyValue(this)
				
				if (withAssociations || !property.type.userDefined)
					loader.load(propertyValue)
				
				values += propertyValue
			}
			
			entity.values = values
			
			if (withAssociations)
				entity.referringEntities = loader.loadReferringEntities
			
			return entity
		} else if (withAssociations && cachedEntity.reference) {
			for (IFXPropertyValue<?> propertyValue : cachedEntity.values)
				loader.load(propertyValue)
			
			cachedEntity.referringEntities = loader.loadReferringEntities
		}
		
		cachedEntity
	}

	override update(ModelChange<FXEntity,IFXPropertyValue<? extends Object>,FXEntityReference> change) {
		for (entity : change.saved.values) {
			val cachedEntity = cachedEntities.get(entity.id)
			
			if (cachedEntity != null)
				cachedEntity.assign(entity)
		}
	}

	override <V> createAttributeValue(Property property,
			AbstractAttributeType<V> type) {
		return new FXAttribute<V>(property, type);
	}

	override createAssociationValue(Property property) {
		return new FXAssociation(property);
	}

	override createAssociationsValue(Property property) {
		return new FXAssociations(property);
	}	
}