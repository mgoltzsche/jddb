package de.algorythm.jdoe.taskQueue;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;

public class Task {

	private final String label;
	private final Procedure0 task;
	
	public Task(final String label, final Procedure0 task) {
		this.label = label;
		this.task = task;
	}

	public String getLabel() {
		return label;
	}

	public void run() {
		task.apply();
	}

	@Override
	public int hashCode() {
		return 31 + ((label == null) ? 0 : label.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
}
