package de.algorythm.jdoe.controller;

import java.util.Collection;
import java.util.LinkedList;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

import javax.inject.Inject;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;

import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.Property;

public class EntityEditorController implements IController {
	
	@FXML private ScrollPane scrollPane;
	@Inject private IDAO dao;
	private IEntity entity;
	private final Collection<Procedure0> saveProcs = new LinkedList<>();
	
	@Override
	public void init() {
	}

	public void setEntity(final IEntity entity) {
		this.entity = entity;
		
		final GridPane gridPane = new GridPane();
		int i = 0;
		
		gridPane.setAlignment(Pos.TOP_LEFT);
		gridPane.setPadding(new Insets(20));
		gridPane.setVgap(20);
		gridPane.setHgap(10);
		
		for (IPropertyValue value : entity.getValues()) {
			final Property property = value.getProperty();
			final Label label = new Label(property.getLabel() + ": ");
			
			gridPane.add(label, 0, i);
			
			property.doWithPropertyValue(value, new EntityEditorControllerHelper(gridPane, i, saveProcs));
			
			i++;
		}
		
		scrollPane.setContent(gridPane);
	}
	
	public void save() {
		for (Procedure0 saveProc : saveProcs)
			saveProc.apply();
		
		dao.save(entity);
	}
	
	public void cancel() {
		
	}
}
