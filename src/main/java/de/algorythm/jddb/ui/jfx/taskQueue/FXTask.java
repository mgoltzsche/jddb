package de.algorythm.jddb.ui.jfx.taskQueue;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.jddb.taskQueue.CancelTaskException;
import de.algorythm.jddb.taskQueue.ITask;
import de.algorythm.jddb.taskQueue.ITaskPriority;
import de.algorythm.jddb.taskQueue.TaskState;

public class FXTask implements ITask {

	static private final Logger log = LoggerFactory.getLogger(FXTask.class);
	
	private final String id;
	private final String label;
	private final ITaskPriority priority;
	private final Runnable task;
	private String errorMessage;
	private TaskState state;
	private SimpleObjectProperty<TaskState> stateProperty = new SimpleObjectProperty<>(TaskState.QUEUED);
	
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
	public void setState(final TaskState updatedState) {
		state = updatedState;
		
		try {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					stateProperty.set(updatedState);
				}
			});
		} catch(Throwable e) {
			log.error("Cannot set task state " + updatedState + " on task " + id + " due to " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}
	
	@Override
	public TaskState getState() {
		return state;
	}
	
	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
	
	@Override
	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	@Override
	public void requireCompleted() throws CancelTaskException {
		switch(state) {
		case COMPLETED: break;
		case FAILED: throw new CancelTaskException("required task " + label + " failed");
		case CANCELED: throw new CancelTaskException("required task " + label + " is canceled");
		default: throw new IllegalStateException("Invalid API usage. " + label + " didn't run");
		}
	}
	
	public ReadOnlyObjectProperty<TaskState> stateProperty() {
		return stateProperty;
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
