package de.algorythm.jdoe.ui.jfx.taskQueue;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import de.algorythm.jdoe.taskQueue.ITask;
import de.algorythm.jdoe.taskQueue.TaskState;

public class FXTask implements ITask {

	private final String id;
	private final String label;
	private final Runnable task;
	private SimpleObjectProperty<TaskState> state = new SimpleObjectProperty<>(TaskState.QUEUED);
	
	public FXTask(final String id, final String label, final Runnable task) {
		this.id = id;
		this.label = label;
		this.task = task;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public void run() {
		task.run();
	}

	@Override
	public void setState(final TaskState state) {
		this.state.set(state);
	}
	
	@Override
	public TaskState getState() {
		return state.get();
	}
	
	@Override
	public boolean isFailed() {
		return state.get() == TaskState.FAILED;
	}
	
	public ReadOnlyObjectProperty<TaskState> stateProperty() {
		return state;
	}
	
	@Override
	public String toString() {
		return label + "(" + id + ")";
	}
}
