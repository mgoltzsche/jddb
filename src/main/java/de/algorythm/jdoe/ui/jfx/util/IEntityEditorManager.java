package de.algorythm.jdoe.ui.jfx.util;

import java.util.List;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import de.algorythm.jdoe.controller.IEntitySaveResult;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public interface IEntityEditorManager {

	/**
	 * Returns the (created) view ID 
	 * @param entity to edit
	 * @param saveCallback is called instead of saving the entity if not null 
	 * @return the view ID
	 */
	void showEntityEditor(FXEntityReference entityRef, Procedure1<IEntitySaveResult> saveCallback);
	void showEntityEditor(FXEntityReference entityRef);
	void closeEntityEditor(FXEntityReference entityRef);
	void closeAll();
	List<String> getOpenEditorIDs();
}
