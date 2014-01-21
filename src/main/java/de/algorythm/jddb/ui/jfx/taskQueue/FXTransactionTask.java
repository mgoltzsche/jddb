package de.algorythm.jddb.ui.jfx.taskQueue;

import javafx.application.Platform;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import de.algorythm.jddb.model.dao.IDAOTransactionContext;
import de.algorythm.jddb.ui.jfx.model.FXEntity;
import de.algorythm.jddb.ui.jfx.model.FXEntityReference;
import de.algorythm.jddb.ui.jfx.model.propertyValue.IFXPropertyValue;

public class FXTransactionTask {

	private final FXEntity entity;
	private Procedure1<IDAOTransactionContext<FXEntity,IFXPropertyValue<?>,FXEntityReference>> task;
	private Procedure0 onBefore;
	private Procedure0 onSuccess;
	private Procedure0 onFailed;
	
	public FXTransactionTask(final FXEntity entity) {
		this.entity = entity;
	}
	
	public FXEntity getEntity() {
		return entity;
	}
	
	public void run(final IDAOTransactionContext<FXEntity,IFXPropertyValue<?>,FXEntityReference> tx) {
		task.apply(tx);
	}
	
	public void onBefore() {
		onBefore.apply();
	}
	
	public void onSuccess() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				onSuccess.apply();
			}
		});
	}
	
	public void onFailed() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				onFailed.apply();
			}
		});
	}
	
	public void setTask(
			final Procedure1<IDAOTransactionContext<FXEntity, IFXPropertyValue<?>, FXEntityReference>> task) {
		this.task = task;
	}
	
	public void setOnBefore(final Procedure0 onBefore) {
		this.onBefore = onBefore;
	}
	
	public void setOnSuccess(final Procedure0 onSuccess) {
		this.onSuccess = onSuccess;
	}
	
	public void setOnFailed(final Procedure0 onFailed) {
		this.onFailed = onFailed;
	}
}