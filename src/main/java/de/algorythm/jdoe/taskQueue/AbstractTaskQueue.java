package de.algorythm.jdoe.taskQueue;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTaskQueue<T extends ITask> {
	
	static private final Logger log = LoggerFactory.getLogger(AbstractTaskQueue.class);
	static private final String THREAD_NAME = "task-thread";
	
	
	private boolean run = true;
	protected final LinkedHashMap<String, T> taskMap = new LinkedHashMap<>();
	protected T currentTask;
	protected final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			while (run || !taskMap.isEmpty()) {
				synchronized(runnable) {
					final Iterator<T> iter = taskMap.values().iterator();
					currentTask = iter.hasNext() ? iter.next() : null;
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
					currentTask.setState(TaskState.RUNNING);
					final String taskLabel = currentTask.getLabel();
					log.debug("run task: " + taskLabel);
					try {
						currentTask.run();
						finishTask(TaskState.COMPLETED);
					} catch(Throwable e) {
						log.error("task " + taskLabel + " failed", e);
						finishTask(TaskState.FAILED);
					}
					
					
				}
			}
		}
	};
	private final Thread taskThread = new Thread(runnable, THREAD_NAME);
	
	public AbstractTaskQueue() {
		taskThread.start();
	}
	
	private void finishTask(final TaskState state) {
		synchronized(runnable) {
			currentTask.setState(state);
			taskMap.remove(currentTask.getId());
			onTaskFinished(currentTask);
		}
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
	protected abstract void onTaskFinished(T task);
}