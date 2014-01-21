package de.algorythm.jddb.taskQueue;

import java.util.List;

public interface ITaskPriority {

	static public final ITaskPriority FIRST = new ITaskPriority() {
		@Override
		public <T extends ITask> void add(final T task, final List<T> queue) {
			TaskPriorityUtil.removeTaskIfNotRunning(task, queue);
			
			if (queue.isEmpty())
				queue.add(task);
			else
				queue.add(1, task);
		}
	};
	
	static public final ITaskPriority LAST = new ITaskPriority() {
		@Override
		public <T extends ITask> void add(final T task, final List<T> queue) {
			TaskPriorityUtil.removeTaskIfNotRunning(task, queue);
			queue.add(task);
		}
	};
	
	public <T extends ITask> void add(T task, List<T> queue);
}
