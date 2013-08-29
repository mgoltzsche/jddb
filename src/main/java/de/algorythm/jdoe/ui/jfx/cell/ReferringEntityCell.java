package de.algorythm.jdoe.ui.jfx.cell;

import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import de.algorythm.jdoe.JavaDbObjectEditorFacade;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;

public class ReferringEntityCell extends ListCell<FXEntityReference> {
	
	static public class Factory implements Callback<ListView<FXEntityReference>, ListCell<FXEntityReference>> {
		
		private final JavaDbObjectEditorFacade facade;
		
		public Factory(final JavaDbObjectEditorFacade facade) {
			this.facade = facade;
		}
		
		@Override
		public ListCell<FXEntityReference> call(final ListView<FXEntityReference> view) {
			return new ReferringEntityCell(facade);
		}
	};
	
	
	private FXEntityReference entityRef;
	
	private ReferringEntityCell(final JavaDbObjectEditorFacade facade) {
		setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent evt) {
				if (entityRef != null)
					facade.showEntityEditor(entityRef);
			}
		});
	}
	
	@Override
	public void updateItem(final FXEntityReference entityRef, final boolean empty) {
		super.updateItem(entityRef, empty);
		
		this.entityRef = entityRef;
		
		if (empty)
			textProperty().unbind();
		else
			textProperty().bind(entityRef.labelProperty());
	}
}