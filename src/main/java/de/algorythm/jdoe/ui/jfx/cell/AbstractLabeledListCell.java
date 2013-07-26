package de.algorythm.jdoe.ui.jfx.cell;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import de.algorythm.jdoe.model.meta.ILabeledElement;

public abstract class AbstractLabeledListCell<T extends ILabeledElement> extends ListCell<T> {

	protected TextField labelInput = new TextField();
	protected T object;
	
	public AbstractLabeledListCell() {
		labelInput.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> valueContainer,
					String oldLabel, String newLabel) {
				updateObjectLabel(newLabel);
			}
		});
	}
	
	protected void updateObjectLabel(final String label) {
		object.setLabel(label);
	}
	
	@Override
	public void startEdit() {
		System.out.println("start edit " + object.getLabel());
		if (getListView().isEditable()) {
			super.startEdit();
			labelInput.setText(object.getLabel());
			setText(null);
			showEditor();
			labelInput.requestFocus();
		}
	}
	
	@Override
	public void cancelEdit() {
		System.out.println("stop edit");
		super.cancelEdit();
		showLabel();
		setGraphic(null);
	}
	
	@Override
	public void updateItem(T object, boolean empty) {
		super.updateItem(object, empty);
		
		this.object = object;
		
		if (empty) {
			setText(null);
			setGraphic(null);
		} else
			showLabel();
	}
	
	protected void showLabel() {
		setText(object.getLabel());
	}
	
	protected void showEditor() {
		setGraphic(labelInput);
	}
}
