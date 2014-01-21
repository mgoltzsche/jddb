package de.algorythm.jddb.taskQueue;

import de.algorythm.jddb.bundle.Bundle;

public enum TaskState {

	QUEUED(Bundle.getInstance().stateQueued, 2),
	RUNNING(Bundle.getInstance().stateRunning, 1),
	CANCELED(Bundle.getInstance().stateCanceled, 3),
	COMPLETED(Bundle.getInstance().stateCompleted, 4),
	FAILED(Bundle.getInstance().stateFailed, 5);
	
	private final String label;
	private final int value;
	
	TaskState(final String label, final int value) {
		this.label = label;
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return label;
	}
}
