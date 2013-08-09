package de.algorythm.jdoe.controller;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import de.algorythm.jdoe.model.entity.IEntity;

public interface IEntityEditorManager {

	/**
	 * Returns the (created) view ID 
	 * @param entity to edit
	 * @param saveCallback is called instead of saving the entity if not null 
	 * @return the view ID
	 */
	void showEntityEditor(IEntity entity, Procedure1<IEntity> saveCallback);
	void showEntityEditor(IEntity entity);
	void closeEntityEditor(IEntity entity);
}
