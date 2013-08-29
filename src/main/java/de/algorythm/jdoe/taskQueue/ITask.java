package de.algorythm.jdoe.taskQueue;

public interface ITask {

	String getId();
	String getLabel();
	void setState(TaskState state);
	void run() throws Exception;
}
