package de.algorythm.jddb.ui.jfx.cell;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import de.algorythm.jddb.bundle.Bundle;
import de.algorythm.jddb.bundle.ImageBundle;
import de.algorythm.jddb.ui.jfx.model.meta.FXAbstractLabeledElement;

public abstract class AbstractLabeledListCell<T extends FXAbstractLabeledElement> extends ListCell<T> {

	static protected final Bundle bundle = Bundle.getInstance();
	
	static protected interface BindingHandler<T extends FXAbstractLabeledElement> {
		<V> void doWithProperty(Property<V> inputProperty, V value, ChangeListener<V> changeListener);
		<V> void doWithProperty(SelectionModel<V> inputProperty, V value, ChangeListener<V> changeListener);
	}
	
	private final BindingHandler<T> binder = new BindingHandler<T>() {
		@Override
		public <V> void doWithProperty(Property<V> inputProperty, V value,
				ChangeListener<V> changeListener) {
			inputProperty.setValue(value);
			inputProperty.addListener(changeListener);
		}
		@Override
		public <V> void doWithProperty(SelectionModel<V> model,
				V value, ChangeListener<V> changeListener) {
			model.select(value);
			model.selectedItemProperty().addListener(changeListener);
		}
	};
	
	private final BindingHandler<T> unbinder = new BindingHandler<T>() {
		@Override
		public <V> void doWithProperty(Property<V> inputProperty, V value,
				ChangeListener<V> changeListener) {
			inputProperty.removeListener(changeListener);
		}
		@Override
		public <V> void doWithProperty(SelectionModel<V> model,
				V value, ChangeListener<V> changeListener) {
			model.selectedItemProperty().removeListener(changeListener);
		}
	};
	
	private final ChangeListener<String> labelListener = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> c,
				String o, String value) {
			editItem.setLabel(value);
		}
	};
	
	private T editItem;
	protected final VBox editor = new VBox();
	protected final TextField labelInput = new TextField();
	protected final Button deleteButton = new Button(bundle.delete);
	
	public AbstractLabeledListCell() {
		deleteButton.setGraphic(new ImageView(ImageBundle.getInstance().delete));
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				getListView().getItems().remove(editItem);
			}
		});
		
		final ObservableList<Node> editorChildren = editor.getChildren();
		
		editorChildren.add(labelInput);
		editorChildren.add(deleteButton);
	}
	
	protected T getEditItem() {
		return editItem;
	}
	
	@Override
	public void startEdit() {
		super.startEdit();
		editItem = getItem();
		hideLabel();
		labelInput.setText(editItem.getLabel());
		setGraphic(editor);
		doWithProperties(binder);
		labelInput.requestFocus();
	}
	
	@Override
	public void cancelEdit() {
		super.cancelEdit();
		showLabel();
		doWithProperties(unbinder);
		setGraphic(null);
		editItem = null;
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
	
	protected void showLabel() {
		textProperty().bind(getItem().labelProperty());
	}
	
	private void hideLabel() {
		final Property<String> p = textProperty();
		
		p.unbind();
		p.setValue(null);
	}
	
	protected void doWithProperties(final BindingHandler<T> binder) {
		binder.doWithProperty(labelInput.textProperty(), editItem.getLabel(), labelListener);
	}
}
