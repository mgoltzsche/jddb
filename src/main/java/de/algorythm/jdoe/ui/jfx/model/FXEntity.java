package de.algorythm.jdoe.ui.jfx.model;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.EntityType;

public class FXEntity {

	private final IEntity entity;
	
	
	public FXEntity(final IEntity entity) {
		this.entity = entity;
	}
	
	public IEntity getEntity() {
		return entity;
	}
	
	public String getId() {
		return entity.getId();
	}

	public EntityType getType() {
		return entity.getType();
	}
	
	/*public Iterable<IPropertyValue<?>> getValues() {
		
	}*/
	
	public IPropertyValue<?> getValue(int index) {
		return entity.getValue(index);
	}
}
