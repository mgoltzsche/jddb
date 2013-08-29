package de.algorythm.jdoe.taskQueue;

import de.algorythm.jdoe.bundle.Bundle;

public enum TaskState {

	QUEUED(Bundle.getInstance().stateQueued),
	RUNNING(Bundle.getInstance().stateRunning),
	COMPLETED(Bundle.getInstance().stateCompleted),
	FAILED(Bundle.getInstance().stateFailed);
	
	private String label;
	
	TaskState(final String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return label;
	}
}
