package de.algorythm.jdoe.controller;

import java.util.Collection;
import java.util.LinkedList;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

import javax.inject.Inject;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.dao.IObserver;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;

public class EntityEditorController implements IController, IObserver {
	
	@Inject private IDAO dao;
	@FXML private ScrollPane scrollPane;
	private IEntity entity;
	private final Collection<Procedure0> propertySaveCallbacks = new LinkedList<>();
	private final Collection<Procedure0> propertyUpdateCallbacks = new LinkedList<>();
	private Procedure1<IEntity> saveCallback;
	private final Collection<IEntity> createdContainedEntities = new LinkedList<>();
	private IEntityEditorManager editorManager;
	
	@Override
	public void init() {
	}
	
	public void init(final IEntity entity, final IEntityEditorManager editorManager, final Procedure1<IEntity> saveCallback) {
		this.entity = entity;
		this.saveCallback = saveCallback;
		this.editorManager = editorManager;
		
		final GridPane gridPane = new GridPane();
		int i = 0;
		
		gridPane.setPadding(new Insets(20));
		gridPane.setVgap(20);
		gridPane.setHgap(10);
		
		for (IPropertyValue value : entity.getValues()) {
			final Label label = new Label(value.getProperty().getLabel() + ": ");
			
			GridPane.setValignment(label, VPos.TOP);
			
			gridPane.add(label, 0, i);
			
			value.doWithValue(new PropertyValueEditorVisitor(gridPane, i, dao, editorManager, createdContainedEntities, propertySaveCallbacks, propertyUpdateCallbacks));
			
			i++;
		}
		
		update();
		
		scrollPane.setContent(gridPane);
		
		// add/remove observer
		dao.addObserver(this);
	}
	
	public String getLabel() {
		return entity.getId() == null
				? entity.getType().getLabel() + " (neu)"
				: entity.getType().getLabel() + ": " + entity;
	}
	
	public void save() {
		for (Procedure0 callback : propertySaveCallbacks)
			callback.apply();
		
		if (saveCallback == null)
			dao.save(entity);
		else
			saveCallback.apply(entity);
	}
	
	public void cancel() {
		
	}

	@Override
	public void update() {
		for (Procedure0 callback : propertyUpdateCallbacks)
			callback.apply();
	}
	
	public void close() {
		dao.removeObserver(this);
		
		for (IEntity entity : createdContainedEntities)
			if (entity.getId() == null)
				editorManager.closeEntityEditor(entity);
	}
}
