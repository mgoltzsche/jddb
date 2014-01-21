package de.algorythm.jddb.ui.jfx.comparator;

import java.util.Comparator;

import de.algorythm.jddb.ui.jfx.model.FXEntityReference;

public class EntityLabelComparator implements Comparator<FXEntityReference> {

	@Override
	public int compare(final FXEntityReference a, final FXEntityReference b) {
		final String aStr = a.labelProperty().get();
		final String bStr = b.labelProperty().get();
		
		System.out.println(aStr + "   " + bStr);
		
		return aStr.compareTo(bStr);
	}
}