package de.algorythm.jddb.ui.jfx.model.meta;

import javafx.beans.InvalidationListener;

public class FXAbstractLabeledChangable extends FXAbstractLabeledElement {

	protected final InvalidationListener invalidationListener;
	
	public FXAbstractLabeledChangable(final InvalidationListener invalidationListener) {
		this.invalidationListener = invalidationListener;
		labelProperty.addListener(invalidationListener);
	}
}
