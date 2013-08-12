package de.algorythm.jdoe.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

import javax.inject.Inject;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import de.algorythm.jdoe.taskQueue.TaskQueue;

public abstract class AbstractXtendController {
	
	@Inject private TaskQueue taskQueue;
	
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
	
	protected void runTask(final String label, final Procedure0 task) {
		taskQueue.runTask(label, task);
	}
	
	protected void runReplacedTask(final String label, final Procedure0 task) {
		taskQueue.runReplacedTask(label, task);
	}
}
