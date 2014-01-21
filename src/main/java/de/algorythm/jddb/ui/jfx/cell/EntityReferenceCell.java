package de.algorythm.jddb.ui.jfx.cell;

import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import de.algorythm.jddb.JavaDesktopDatabaseFacade;
import de.algorythm.jddb.ui.jfx.model.FXEntityReference;

public class EntityReferenceCell extends ListCell<FXEntityReference> {
	
	static public class Factory implements Callback<ListView<FXEntityReference>, ListCell<FXEntityReference>> {
		
		private final JavaDesktopDatabaseFacade facade;
		
		public Factory(final JavaDesktopDatabaseFacade facade) {
			this.facade = facade;
		}
		
		@Override
		public ListCell<FXEntityReference> call(final ListView<FXEntityReference> view) {
			return new EntityReferenceCell(facade);
		}
	};
	
	
	protected EntityReferenceCell(final JavaDesktopDatabaseFacade facade) {
		setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent evt) {
				final FXEntityReference entityRef = getItem();
				
				if (entityRef != null)
					facade.showEntityEditor(entityRef);
			}
		});
		setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent evt) {
				final FXEntityReference entityRef = getItem();
				
				if (entityRef != null)
					facade.showEntityDetails(entityRef, EntityReferenceCell.this);
			}
		});
	}
	
	@Override
	public void updateItem(final FXEntityReference entityRef, final boolean empty) {
		super.updateItem(entityRef, empty);
		
		if (empty)
			textProperty().unbind();
		else
			textProperty().bind(entityRef.labelProperty());
	}
}