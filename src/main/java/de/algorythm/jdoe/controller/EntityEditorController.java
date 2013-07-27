package de.algorythm.jdoe.controller;

import java.util.Collection;
import java.util.LinkedList;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

import javax.inject.Inject;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;

import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.dao.IObserver;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.Property;

public class EntityEditorController implements IController, IObserver {
	
	@Inject private IDAO dao;
	@FXML private ScrollPane scrollPane;
	private IEntity entity;
	private final Collection<Procedure0> saveCallbacks = new LinkedList<>();
	private final Collection<Procedure0> updateCallbacks = new LinkedList<>();
	private Tab tab;
	
	@Override
	public void init() {
	}

	public void init(final IEntity entity, final Tab tab) {
		this.entity = entity;
		this.tab = tab;
		
		final GridPane gridPane = new GridPane();
		int i = 0;
		
		gridPane.setPadding(new Insets(20));
		gridPane.setVgap(20);
		gridPane.setHgap(10);
		
		for (IPropertyValue value : entity.getValues()) {
			final Label label = new Label(value.getProperty().getLabel() + ": ");
			
			GridPane.setValignment(label, VPos.TOP);
			
			gridPane.add(label, 0, i);
			
			value.doWithValue(new PropertyValueEditorVisitor(gridPane, i, dao, saveCallbacks, updateCallbacks));
			
			i++;
		}
		
		update();
		
		scrollPane.setContent(gridPane);
		
		// add/remove observer
		dao.addObserver(this);
		
		tab.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(Event evt) {
				dao.removeObserver(EntityEditorController.this);
			}
		});
	}
	
	public void save() {
		for (Procedure0 saveCallback : saveCallbacks)
			saveCallback.apply();
		
		dao.save(entity);
		
		tab.setId(entity.getId());
		tab.setText(entity.getType().getLabel() + ": " + entity.toString());
	}
	
	public void cancel() {
		
	}

	@Override
	public void update() {
		for (Procedure0 updateCallback : updateCallbacks)
			updateCallback.apply();
	}
}
