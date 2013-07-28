package de.algorythm.jdoe.controller;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import de.algorythm.jdoe.model.entity.IEntity;

public interface IEntityEditorManager {

	void showEntityEditor(final IEntity entity, final Procedure1<IEntity> saveCallback);
	void closeEntityEditor(final IEntity entity);
}
