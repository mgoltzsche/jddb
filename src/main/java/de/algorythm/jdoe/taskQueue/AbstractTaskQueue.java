package de.algorythm.jdoe.taskQueue;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTaskQueue<T extends ITask> {
	
	static private final Logger log = LoggerFactory.getLogger(AbstractTaskQueue.class);
	
	private boolean run = true;
	private final LinkedList<T> taskQueue = new LinkedList<>();
	private final Runnable runnable;
	private final Thread taskThread;
	
	public AbstractTaskQueue(final String name) {
		// create task thread
		runnable = new Runnable() {
			@Override
			public void run() {
				while (run || !taskQueue.isEmpty()) {
					final T currentTask;
					
					synchronized(runnable) {
						final Iterator<T> iter = taskQueue.iterator();
						
						if (iter.hasNext()) {
							currentTask = iter.next();
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
						currentTask.setState(TaskState.RUNNING);
						final String taskLabel = currentTask.getLabel();
						log.debug(taskLabel);
						
						try {
							currentTask.run();
							currentTask.setState(TaskState.COMPLETED);
						} catch(Throwable e) {
							log.error("task " + taskLabel + " failed", e);
							currentTask.setState(TaskState.FAILED);
						} finally {
							synchronized(runnable) {
								taskQueue.remove(currentTask);
								onTaskRemoved(currentTask);
							}
						}
					}
				}
			}
		};
		taskThread = new Thread(runnable, name);
		taskThread.start();
		
		// stop taskThread on application shutdown (SIGTERM)
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
	
	public List<T> getPendingTasks() {
		return Collections.unmodifiableList(taskQueue);
	}
	
	public void runTask(final T task) {
		synchronized(runnable) {
			taskQueue.remove(task);
			task.getPriority().add(taskQueue, task);
			runnable.notify();
			onTaskQueued(task);
		}
	}
	
	protected void onTaskQueued(T task) {}
	
	protected void onTaskRemoved(T task) {}
}