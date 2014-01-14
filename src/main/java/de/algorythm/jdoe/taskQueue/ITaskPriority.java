package de.algorythm.jdoe.taskQueue;

import java.util.List;


public interface ITaskPriority {

	static public final ITaskPriority HIGHER = new ITaskPriority() {
		@Override
		public <T extends ITask> void add(T task, List<T> queue) {
			queue.add(0, task);
		}
	};
	
	static public final ITaskPriority LOWER = new ITaskPriority() {
		@Override
		public <T extends ITask> void add(T task, List<T> queue) {
			queue.add(task);
		}
	};
	
	public <T extends ITask> void add(T task, List<T> queue);
}
