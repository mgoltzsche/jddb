package de.algorythm.jddb.taskQueue;

public class CancelTaskException extends Exception {

	static private final long serialVersionUID = 339765679275333190L;

	public CancelTaskException(final String message) {
		super(message);
	}
}
