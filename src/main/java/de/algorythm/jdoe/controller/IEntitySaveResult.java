package de.algorythm.jdoe.controller;

import java.util.Collection;

import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTransactionTask;

public interface IEntitySaveResult {

	FXEntityReference getEntityReference();
	Collection<FXTransactionTask> saveLater();
}
