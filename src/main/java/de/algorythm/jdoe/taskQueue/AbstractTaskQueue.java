package de.algorythm.jdoe.taskQueue;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTaskQueue<T extends ITask> {
	
	static private final Logger log = LoggerFactory.getLogger(AbstractTaskQueue.class);
	static private final String THREAD_NAME = "task-queue-";
	static private int instanceCount = 0;
	
	
	private boolean run = true;
	private final LinkedHashMap<String, T> taskMap = new LinkedHashMap<>();
	private T currentTask;
	private final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			while (run || !taskMap.isEmpty()) {
				synchronized(runnable) {
					final Iterator<T> iter = taskMap.values().iterator();
					
					if (iter.hasNext()) {
						currentTask = iter.next();
						taskMap.remove(currentTask.getId());
					} else
						currentTask = null;
				}
				
				if (currentTask == null) {
					try {
						synchronized(this) {
							wait();
						}
					} catch (InterruptedException e) {
						log.debug(THREAD_NAME + " interrupted");
					}
				} else {
					onTaskStarted(currentTask);
					final String taskLabel = currentTask.getLabel();
					log.debug("run task: " + taskLabel);
					try {
						currentTask.run();
						onTaskFinished(currentTask, TaskState.COMPLETED);
					} catch(Throwable e) {
						log.error("task " + taskLabel + " failed", e);
						onTaskFinished(currentTask, TaskState.FAILED);
					}
				}
			}
		}
	};
	private final Thread taskThread = new Thread(runnable, THREAD_NAME + (instanceCount++));
	
	public AbstractTaskQueue() {
		taskThread.start();
	}
	
	protected Collection<T> getPendingTasks() {
		final LinkedList<T> pendingTasks = new LinkedList<>();
		
		synchronized(runnable) {
			if (currentTask != null)
				pendingTasks.add(currentTask);
			
			pendingTasks.addAll(taskMap.values());
		}
		
		return pendingTasks;
	}
	
	public void close() {
		run = false;
		
		synchronized(runnable) {
			runnable.notify();
		}
	}
	
	public void runTask(final T task) {
		synchronized(runnable) {
			taskMap.put(task.getId(), task);
			runnable.notify();
			onTaskQueued(task);
		}
	}
	
	protected abstract void onTaskQueued(T task);
	
	protected void onTaskStarted(final T task) {
		task.setState(TaskState.RUNNING);
	}
	
	protected void onTaskFinished(final T task, final TaskState state) {
		task.setState(state);
	}
}