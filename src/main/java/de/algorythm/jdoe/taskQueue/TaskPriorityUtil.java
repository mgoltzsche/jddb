package de.algorythm.jdoe.taskQueue;

import java.util.Iterator;
import java.util.List;

public class TaskPriorityUtil {
	
	static public <T extends ITask> void removeTaskIfNotRunning(final T task, final List<T> queue) {
		final Iterator<T> queueIter = queue.iterator();
		
		while (queueIter.hasNext()) {
			final T queuedTask = queueIter.next();
			
			if (queuedTask.equals(task) && queuedTask.getState() != TaskState.RUNNING) {
				queueIter.remove();
				return;
			}
		}
	}
}
