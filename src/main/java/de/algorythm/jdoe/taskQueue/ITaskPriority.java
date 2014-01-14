package de.algorythm.jdoe.taskQueue;

import java.util.LinkedList;

public interface ITaskPriority {

	static public final ITaskPriority HIGHER = new ITaskPriority() {
		@Override
		public <T extends ITask> void add(LinkedList<T> taskQueue, T task) {
			taskQueue.addFirst(task);
		}
	};
	
	static public final ITaskPriority LOWER = new ITaskPriority() {
		@Override
		public <T extends ITask> void add(LinkedList<T> taskQueue, T task) {
			taskQueue.addLast(task);
		}
	};
	
	public <T extends ITask> void add(LinkedList<T> taskQueue, T task);
}
