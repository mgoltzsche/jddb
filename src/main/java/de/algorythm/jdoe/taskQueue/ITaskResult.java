package de.algorythm.jdoe.taskQueue;

public interface ITaskResult extends Runnable {

	TaskState getState();
	boolean isFailed();
}
