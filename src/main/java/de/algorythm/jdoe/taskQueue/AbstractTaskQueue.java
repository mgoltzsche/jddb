package de.algorythm.jdoe.taskQueue;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTaskQueue<T extends ITask> {
	
	static private final Logger log = LoggerFactory.getLogger(AbstractTaskQueue.class);
	
	private boolean run = true;
	private final LinkedHashMap<String, T> taskMap = new LinkedHashMap<>();
	private T currentTask;
	private final Runnable runnable;
	private final Thread taskThread;
	
	public AbstractTaskQueue(final String name) {
		// create task thread
		runnable = new Runnable() {
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
							log.debug(name + " interrupted");
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
		taskThread = new Thread(runnable, name);
		taskThread.start();
		
		// register close listener
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				run = false;
				
				try {
					synchronized(runnable) {
						log.debug("Finish current pending tasks and terminate " + name);
						runnable.notify();
					}
				} catch(Throwable e) {
					log.error("Failed to notify task thread " + name + " to finish and terminate");
				}
			}
		}));
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
	
	public void runTask(final T task) {
		synchronized(runnable) {
			taskMap.put(task.getId(), task);
			runnable.notify();
			onTaskQueued(task);
		}
	}
	
	protected void onTaskQueued(T task) {}
	
	protected void onTaskStarted(final T task) {
		task.setState(TaskState.RUNNING);
	}
	
	protected void onTaskFinished(final T task, final TaskState state) {
		task.setState(state);
	}
}