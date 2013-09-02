package de.algorythm.jdoe.ui.jfx.comparator;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {

	static public final StringComparator INSTANCE = new StringComparator();
	
	private StringComparator() {}
	
	@Override
	public int compare(final String a, final String b) {
		return a.compareToIgnoreCase(b);
	}
}
