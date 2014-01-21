package de.algorythm.jddb.controller;

import java.util.Collection;

import de.algorythm.jddb.ui.jfx.model.FXEntityReference;
import de.algorythm.jddb.ui.jfx.taskQueue.FXTransactionTask;

public interface IEntitySaveResult {

	FXEntityReference getEntityReference();
	Collection<FXTransactionTask> saveLater();
}
