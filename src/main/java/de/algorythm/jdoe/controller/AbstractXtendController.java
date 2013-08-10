package de.algorythm.jdoe.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractXtendController {

	static private final Logger log = LoggerFactory.getLogger(AbstractXtendController.class);
	
	protected void actionListener(final Button button, final Procedure0 listener) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent evt) {
				listener.apply();
			}
		});
	}
	
	protected void actionListener(final MenuItem item, final Procedure0 listener) {
		item.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent evt) {
				listener.apply();
			}
		});
	}
	
	protected <V> void changeListener(final ObservableValue<V> value, final Procedure1<V> listener) {
		value.addListener(new ChangeListener<V>() {
			@Override
			public void changed(ObservableValue<? extends V> ValueContainer,
					V oldValue, V newValue) {
				if (oldValue != newValue)
					listener.apply(newValue);
			}
		});
	}
	
	protected <V> void changeListener(final ObservableList<V> values, final Procedure1<Change<? extends V>> listener) {
		values.addListener(new ListChangeListener<V>() {
			@Override
			public void onChanged(Change<? extends V> change) {
				listener.apply(change);
			}
		});
	}
	
	protected void onClosedListener(final Tab tab, final Procedure0 listener) {
		tab.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(Event evt) {
				listener.apply();
			}
		});
	}
	
	protected void runLater(final Procedure0 procedure) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				procedure.apply();
			}
		});
	}
	
	protected void runTask(final Procedure0 procedure) {
		final Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					procedure.apply();
				} catch(Throwable e) {
					log.error("Task failed", e);
				}
				
				return null;
			}
		};
		
		new Thread(task).start();
	}
}
