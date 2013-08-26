package de.algorythm.jdoe.ui.jfx.model;

import javafx.scene.control.Tab;
import de.algorythm.jdoe.controller.EntityEditorController;

public class ViewData {

	private final Tab tab;
	private final FXEntityReference entityRef;
	private final EntityEditorController controller;
	
	public ViewData(final Tab tab, final FXEntityReference entityRef, final EntityEditorController controller) {
		this.tab = tab;
		this.entityRef = entityRef;
		this.controller = controller;
	}
	
	public Tab getTab() {
		return tab;
	}
	
	public FXEntityReference getEntityRef() {
		return entityRef;
	}
	
	public EntityEditorController getController() {
		return controller;
	}
}
