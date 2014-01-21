package de.algorythm.jddb.taskQueue;

public interface ITaskResult {

	TaskState getState();
	String getErrorMessage();
	void requireCompleted() throws CancelTaskException;
}
