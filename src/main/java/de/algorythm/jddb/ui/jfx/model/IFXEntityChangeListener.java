package de.algorythm.jddb.ui.jfx.model;

public interface IFXEntityChangeListener {

	static public final IFXEntityChangeListener DEFAULT = new IFXEntityChangeListener() {
		@Override
		public void changed() {
		}
	};
	
	public void changed();
}
