package de.algorythm.jdoe.ui.jfx.taskQueue;

import java.util.Comparator;

public class FXTaskStateComparator implements Comparator<FXTask> {

	static public final FXTaskStateComparator INSTANCE = new FXTaskStateComparator();
	
	@Override
	public int compare(FXTask t1, FXTask t2) {
		return t2.getState().getValue() - t1.getState().getValue();
	}
}
