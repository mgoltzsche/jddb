package de.algorythm.jdoe.ui.jfx.taskQueue;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import de.algorythm.jdoe.taskQueue.ITask;
import de.algorythm.jdoe.taskQueue.ITaskPriority;
import de.algorythm.jdoe.taskQueue.TaskState;

public class FXTask implements ITask {

	private final String id;
	private final String label;
	private final ITaskPriority priority;
	private final Runnable task;
	private SimpleObjectProperty<TaskState> state = new SimpleObjectProperty<>(TaskState.QUEUED);
	
	public FXTask(final String id, final String label, final ITaskPriority priority, final Runnable task) {
		this.id = id;
		this.label = label;
		this.priority = priority;
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
	public ITaskPriority getPriority() {
		return priority;
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
	
	public ReadOnlyObjectProperty<TaskState> stateProperty() {
		return state;
	}
	
	@Override
	public String toString() {
		return label + "(" + id + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FXTask other = (FXTask) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
