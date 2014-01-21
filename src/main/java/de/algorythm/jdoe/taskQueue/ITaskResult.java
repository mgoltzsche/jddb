package de.algorythm.jdoe.taskQueue;

public interface ITaskResult {

	TaskState getState();
	String getErrorMessage();
	void requireCompleted() throws CancelTaskException;
}
