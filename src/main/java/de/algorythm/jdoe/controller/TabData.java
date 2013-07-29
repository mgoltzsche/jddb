package de.algorythm.jdoe.controller;

import javafx.scene.control.Tab;

public class TabData {

	private final Tab tab;
	private final EntityEditorController controller;
	
	public TabData(final Tab tab, final EntityEditorController controller) {
		this.tab = tab;
		this.controller = controller;
	}
	
	public Tab getTab() {
		return tab;
	}
	
	public EntityEditorController getController() {
		return controller;
	}
}
