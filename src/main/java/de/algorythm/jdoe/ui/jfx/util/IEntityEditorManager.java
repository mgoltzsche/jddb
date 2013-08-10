package de.algorythm.jdoe.ui.jfx.util;

import java.util.Collection;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;

public interface IEntityEditorManager {

	/**
	 * Returns the (created) view ID 
	 * @param entity to edit
	 * @param saveCallback is called instead of saving the entity if not null 
	 * @return the view ID
	 */
	void showEntityEditor(FXEntity entity, Procedure1<FXEntity> saveCallback);
	void showEntityEditor(FXEntity entity);
	void closeEntityEditor(FXEntity entity);
	FXEntity wrap(IEntity entity);
	Collection<FXEntity> wrap(Iterable<IEntity> entities);
}
