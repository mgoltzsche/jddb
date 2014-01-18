package de.algorythm.jdoe.ui.jfx.cell;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import org.eclipse.xtext.xbase.lib.Functions.Function1;

import de.algorythm.jdoe.bundle.Bundle;
import de.algorythm.jdoe.ui.jfx.model.meta.FXAbstractLabeledElement;

public abstract class AbstractLabeledListCell<T extends FXAbstractLabeledElement> extends ListCell<T> {

	static protected final Bundle bundle = Bundle.getInstance();
	
	static protected interface BindingHandler<T extends FXAbstractLabeledElement> {
		<V> void doWithProperty(Property<V> targetProperty, Function1<T,Property<V>> propertyResolver);
		<V> void doWithProperty(SingleSelectionModel<V> targetSelection, Function1<T,Property<V>> propertyResolver);
	}
	
	static private class Binder<T extends FXAbstractLabeledElement> implements BindingHandler<T>  {
		private final T object;
		public Binder(final T object) {
			this.object = object;
		}
		@Override
		public <V> void doWithProperty(final Property<V> targetProperty, final Function1<T,Property<V>> propertyResolver) {
			final Property<V> newProperty = propertyResolver.apply(object);
			
			targetProperty.setValue(newProperty.getValue());
			newProperty.bind(targetProperty);
		}
		@Override
		public <V> void doWithProperty(final SingleSelectionModel<V> targetSelection,
				final Function1<T, Property<V>> propertyResolver) {
			final Property<V> newProperty = propertyResolver.apply(object);
			
			targetSelection.select(newProperty.getValue());
			newProperty.bind(targetSelection.selectedItemProperty());
		}
	};
	
	static private class Unbinder<T extends FXAbstractLabeledElement> implements BindingHandler<T>  {
		private final T object;
		public Unbinder(final T object) {
			this.object = object;
		}
		@Override
		public <V> void doWithProperty(final Property<V> targetProperty, final Function1<T,Property<V>> propertyResolver) {
			propertyResolver.apply(object).unbind();
		}
		@Override
		public <V> void doWithProperty(final SingleSelectionModel<V> targetSelection,
				final Function1<T, Property<V>> propertyResolver) {
			propertyResolver.apply(object).unbind();
		}
	};
	
	private Function1<T,Property<String>> LABEL_RESOLVER = new Function1<T,Property<String>>() {
		@Override
		public Property<String> apply(T object) {
			return object.labelProperty();
		}
	};
	
	
	private T editObject;
	protected final VBox editor = new VBox();
	protected final TextField labelInput = new TextField();
	protected final Button deleteButton = new Button(bundle.delete);
	
	public AbstractLabeledListCell() {
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				getListView().getItems().remove(editObject);
			}
		});
		
		final ObservableList<Node> editorChildren = editor.getChildren();
		
		editorChildren.add(labelInput);
		editorChildren.add(deleteButton);
	}
	
	protected T getEditObject() {
		return editObject;
	}
	
	@Override
	public void startEdit() {
		super.startEdit();
		System.out.println("start edit " + getItem().getLabel());
		editObject = getItem();
		hideLabel();
		labelInput.setText(editObject.getLabel());
		updateEditorNodes();
		setGraphic(editor);
		handleBinding(new Binder<T>(editObject));
		labelInput.requestFocus();
	}
	
	@Override
	public void cancelEdit() {
		super.cancelEdit();
		System.out.println("stop edit " + editObject);
		showLabel();
		handleBinding(new Unbinder<T>(editObject));
		setGraphic(null);
		editObject = null;
	}
	
	@Override
	public void updateItem(T object, boolean empty) {
		super.updateItem(object, empty);
		
		if (empty) {
			hideLabel();
			setGraphic(null);
		} else
			showLabel();
	}
	
	private void showLabel() {
		textProperty().bind(getItem().labelProperty());
	}
	
	private void hideLabel() {
		final Property<String> p = textProperty();
		
		p.unbind();
		p.setValue(null);
	}
	
	protected void handleBinding(BindingHandler<T> binder) {
		doWithProperties(binder);
	}
	
	protected void doWithProperties(final BindingHandler<T> binder) {
		binder.doWithProperty(labelInput.textProperty(), LABEL_RESOLVER);
	}
	
	protected void updateEditorNodes() {}
}
