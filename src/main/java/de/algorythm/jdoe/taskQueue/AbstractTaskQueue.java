package de.algorythm.jdoe.taskQueue;

import java.util.Iterator;
import java.util.LinkedList;

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
					final T runningTask;
					
					synchronized(this) {
						final Iterator<T> iter = taskQueue.iterator();
						
						if (iter.hasNext()) {
							runningTask = iter.next();
						} else
							runningTask = null;
					}
					
					if (runningTask == null) {
						try {
							synchronized(this) {
								wait();
							}
						} catch (InterruptedException e) {
							log.debug(name + " interrupted");
						}
					} else {
						final String taskLabel = runningTask.getLabel();
						log.debug(taskLabel);
						runningTask.setState(TaskState.RUNNING);
						
						try {
							runningTask.run();
							runningTask.setState(TaskState.COMPLETED);
						} catch(CancelTaskException cte) {
							runningTask.setState(TaskState.CANCELED);
							log.debug(taskLabel + " has been canceled: " + cte.getMessage());
						} catch(Throwable e) {
							log.error(taskLabel + " failed", e);
							runningTask.setErrorMessage(createErrorMessage(e));
							runningTask.setState(TaskState.FAILED);
						} finally {
							synchronized(this) {
								taskQueue.remove(runningTask);
								onTaskRemoved(runningTask);
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
	
	private String createErrorMessage(Throwable e) {
		final StringBuilder sb = new StringBuilder();
		final String exceptionName = e.getClass().getSimpleName();
		
		sb.append(exceptionName).append(": ").append(e.getMessage());
		
		Throwable cause = e;
		Throwable rootCause = null;
		
		while ((cause = cause.getCause()) != null)
			rootCause = cause;
		
		if (rootCause != null) {
			sb.append("\nRoot cause: \n");
			sb.append(exceptionName).append(": ").append(e.getMessage());
		}
		
		return sb.toString();
	}
	
	public void runTask(final T task) {
		synchronized(runnable) {
			task.getPriority().add(task, taskQueue);
			runnable.notify();
			onTaskQueued(task);
		}
	}
	
	protected void onTaskQueued(T task) {}
	
	protected void onTaskRemoved(T task) {}
}