package de.algorythm.jddb.taskQueue;

public interface ITaskQueueExceptionHandler {

	static public final ITaskQueueExceptionHandler DEFAULT = new ITaskQueueExceptionHandler() {
		@Override
		public void handleError(final Throwable e) {
		}
	};
	
	void handleError(Throwable e);
}
