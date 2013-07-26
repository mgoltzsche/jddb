package de.algorythm.jdoe.ui.jfx.cell;

import javafx.event.EventHandler;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import de.algorythm.jdoe.model.entity.IEntity;

public class EntityRow extends TableRow<IEntity> implements EventHandler<MouseEvent> {

	static public class Factory implements
			Callback<TableView<IEntity>, TableRow<IEntity>> {
		
		private final Procedure1<IEntity> actionHandler;
		
		public Factory(final Procedure1<IEntity> actionHandler) {
			this.actionHandler = actionHandler;
		}
		
		@Override
		public TableRow<IEntity> call(TableView<IEntity> view) {
			return new EntityRow(actionHandler);
		}
	}
	
	
	private final Procedure1<IEntity> actionHandler;
	
	public EntityRow(final Procedure1<IEntity> actionHandler) {
		super();
		this.actionHandler = actionHandler;
		addEventHandler(MouseEvent.MOUSE_CLICKED, this);
	}
	
	@Override
	public void handle(MouseEvent evt) {
		if (evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() == 2) {
			final IEntity entity = getItem();
			
			if (entity != null)
				actionHandler.apply(entity);
		}
	}
}
