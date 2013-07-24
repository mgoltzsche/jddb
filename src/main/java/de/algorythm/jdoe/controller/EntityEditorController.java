package de.algorythm.jdoe.controller;

import java.util.Collection;
import java.util.Date;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import javax.inject.Inject;

import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.Property;
import de.algorythm.jdoe.model.meta.visitor.IPropertyValueVisitor;

public class EntityEditorController implements IController {
	
	@FXML private ScrollPane scrollPane;
	@Inject private IDAO dao;
	private IEntity entity;
	
	@Override
	public void init() {
	}

	public void setEntity(final IEntity entity) {
		final GridPane gridPane = new GridPane();
		int i = 0;
		
		gridPane.setAlignment(Pos.TOP_LEFT);
		gridPane.setPadding(new Insets(20));
		gridPane.setVgap(20);
		gridPane.setHgap(10);
		
		for (IPropertyValue value : entity.getValues()) {
			final int row = i;
			final Property property = value.getProperty();
			final Label label = new Label(value.getProperty().getLabel() + ": ");
			
			gridPane.add(label, 0, i);
			
			property.doWithPropertyValue(value, new IPropertyValueVisitor() {
				
				@Override
				public void doWithEntityCollection(IPropertyValue propertyValue,
						Collection<IEntity> values) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void doWithEntity(IPropertyValue propertyValue, IEntity value) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void doWithBoolean(IPropertyValue propertyValue,
						boolean value) {
					final CheckBox checkBox = new CheckBox();
					
					checkBox.setSelected(value);
					
					gridPane.add(checkBox, 1, row);
				}

				@Override
				public void doWithDecimal(IPropertyValue propertyValue,
						Long value) {
					// TODO: check format
					final TextField textField = new TextField(value == null ? null : value.toString());
					
					gridPane.add(textField, 1, row);
				}

				@Override
				public void doWithReal(IPropertyValue propertyValue,
						Double value) {
					// TODO: check format
					final TextField textField = new TextField(value == null ? null : value.toString());
					
					gridPane.add(textField, 1, row);
				}

				@Override
				public void doWithDate(IPropertyValue propertyValue, Date value) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void doWithString(IPropertyValue propertyValue,
						String value) {
					final TextField textField = new TextField(value);
					
					gridPane.add(textField, 1, row);
				}

				@Override
				public void doWithText(IPropertyValue propertyValue,
						String value) {
					final TextArea textArea = new TextArea(value);
					
					gridPane.add(textArea, 1, row);
				}
			});
			
			i++;
		}
		
		scrollPane.setContent(gridPane);
	}
	
	public void save() {
		dao.save(entity);
	}
	
	public void cancel() {
		
	}
}
