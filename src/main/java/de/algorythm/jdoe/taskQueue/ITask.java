package de.algorythm.jdoe.taskQueue;

public interface ITask extends ITaskResult {

	String getId();
	String getLabel();
	ITaskPriority getPriority();
	void setState(TaskState state);
	void setErrorMessage(String errorMessage);
	void run() throws CancelTaskException;
}
