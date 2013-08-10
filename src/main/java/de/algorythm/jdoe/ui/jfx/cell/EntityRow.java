package de.algorythm.jdoe.ui.jfx.cell;

import javafx.event.EventHandler;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import de.algorythm.jdoe.ui.jfx.model.FXEntity;

public class EntityRow extends TableRow<FXEntity> implements EventHandler<MouseEvent> {

	static public class Factory implements
			Callback<TableView<FXEntity>, TableRow<FXEntity>> {
		
		private final Procedure1<FXEntity> actionHandler;
		
		public Factory(final Procedure1<FXEntity> actionHandler) {
			this.actionHandler = actionHandler;
		}
		
		@Override
		public TableRow<FXEntity> call(TableView<FXEntity> view) {
			return new EntityRow(actionHandler);
		}
	}
	
	
	private final Procedure1<FXEntity> actionHandler;
	
	public EntityRow(final Procedure1<FXEntity> actionHandler) {
		super();
		this.actionHandler = actionHandler;
		addEventHandler(MouseEvent.MOUSE_CLICKED, this);
	}
	
	@Override
	public void handle(MouseEvent evt) {
		if (evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() == 2) {
			final FXEntity entity = getItem();
			
			if (entity != null)
				actionHandler.apply(entity);
		}
	}
}
