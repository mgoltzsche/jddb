package de.algorythm.jdoe.controller;

import de.algorythm.jdoe.model.entity.IEntity;
import javafx.scene.control.Tab;

public class TabData {

	private final Tab tab;
	private final IEntity entity;
	
	public TabData(final Tab tab, final IEntity entity) {
		this.tab = tab;
		this.entity = entity;
	}
	
	public Tab getTab() {
		return tab;
	}
	
	public IEntity getEntity() {
		return entity;
	}
}
