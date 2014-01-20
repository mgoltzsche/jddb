package de.algorythm.jdoe.taskQueue;

import de.algorythm.jdoe.bundle.Bundle;

public enum TaskState {

	QUEUED(Bundle.getInstance().stateQueued, 2),
	RUNNING(Bundle.getInstance().stateRunning, 1),
	COMPLETED(Bundle.getInstance().stateCompleted, 3),
	FAILED(Bundle.getInstance().stateFailed, 4);
	
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
