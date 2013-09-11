package de.algorythm.jdoe.taskQueue;

public interface ITask extends ITaskResult {

	String getId();
	String getLabel();
	void setState(TaskState state);
	void run() throws Exception;
}
