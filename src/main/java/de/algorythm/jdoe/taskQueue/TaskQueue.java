package de.algorythm.jdoe.taskQueue;

import java.util.LinkedList;

import javax.inject.Singleton;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.jdoe.taskQueue.ITaskNotifier.NotificationType;

@Singleton
public class TaskQueue {
	
	static private final Logger log = LoggerFactory.getLogger(TaskQueue.class);
	static private final String THREAD_NAME = "task-thread";
	
	
	private ITaskNotifier notifier;
	private boolean run = true;
	private final LinkedList<Task> tasks = new LinkedList<>();
	private final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			Task nextTask;
			
			while (run || !tasks.isEmpty()) {
				nextTask = tasks.pollFirst();
				
				if (nextTask == null) {
					try {
						synchronized(this) {
							wait();
						}
					} catch (InterruptedException e) {
						log.debug(THREAD_NAME + " interrupted");
					}
				} else {
					final String taskLabel = nextTask.getLabel();
					System.out.println("run task: " + nextTask.getLabel());
					try {
						nextTask.run();
						notifier.showNotification(taskLabel, NotificationType.COMPLETED);
					} catch(Throwable e) {
						log.error("task " + nextTask.getLabel() + " failed", e);
						notifier.showNotification(taskLabel, NotificationType.FAILED);
					}
				}
			}
		}
	};
	private final Thread taskThread = new Thread(runnable, THREAD_NAME);
	
	public TaskQueue() {
		taskThread.start();
	}
	
	public void close() {
		run = false;
	}
	
	public void setNotifier(final ITaskNotifier notifier) {
		this.notifier = notifier;
	}
	
	public void runTask(final String label, final Procedure0 task) {
		synchronized(runnable) {
			final Task labeledTask = new Task(label, task);
			
			tasks.add(labeledTask);
			notifier.showNotification(label, NotificationType.PROCESSING);
			runnable.notify();
		}
	}
	
	public void runReplacedTask(final String label, final Procedure0 task) {
		synchronized(runnable) {
			final Task labeledTask = new Task(label, task);
			
			tasks.remove(labeledTask);
			tasks.add(labeledTask);
			runnable.notify();
		}
	}
}