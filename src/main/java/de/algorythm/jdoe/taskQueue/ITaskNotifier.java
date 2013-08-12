package de.algorythm.jdoe.taskQueue;

public interface ITaskNotifier {

	static public enum NotificationType {
		PROCESSING,
		COMPLETED,
		FAILED
	}
	
	static public final ITaskNotifier DEFAULT = new ITaskNotifier() {
		@Override
		public void showNotification(String message, NotificationType type) {}
	};
	
	void showNotification(String message, NotificationType type);
}