package de.algorythm.jdoe.ui.jfx.model;

import de.algorythm.jdoe.controller.EntityEditorController;
import javafx.scene.control.Tab;

public class ViewData {

	private final Tab tab;
	private final EntityEditorController controller;
	
	public ViewData(final Tab tab, final EntityEditorController controller) {
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
