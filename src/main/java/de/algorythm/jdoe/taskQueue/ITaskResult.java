package de.algorythm.jdoe.taskQueue;

public interface ITaskResult {

	TaskState getState();
	boolean isFailed();
}
