package de.algorythm.jdoe.ui.jfx.cell;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;

import de.algorythm.jdoe.ui.jfx.model.FXEntity;

public class EntityRowFactory implements Callback<TableView<FXEntity>, TableRow<FXEntity>> {
		
	private final Procedure1<FXEntity> mouseClickHandler;
	private final Procedure2<FXEntity, Node> mouseEnterHandler;
	
	public EntityRowFactory(final Procedure1<FXEntity> mouseClickHandler, final Procedure2<FXEntity, Node> mouseEnterHandler) {
		this.mouseClickHandler = mouseClickHandler;
		this.mouseEnterHandler = mouseEnterHandler;
	}
	
	@Override
	public TableRow<FXEntity> call(TableView<FXEntity> view) {
		TableRow<FXEntity> row = new TableRow<FXEntity>();
		row.addEventHandler(MouseEvent.MOUSE_CLICKED, new MouseClickListener(row, mouseClickHandler));
		row.addEventHandler(MouseEvent.MOUSE_ENTERED, new MouseEnterListener(row, mouseEnterHandler));
		return row;
	}
	
	static private class MouseClickListener implements EventHandler<MouseEvent> {
		
		private final TableRow<FXEntity> row;
		private final Procedure1<FXEntity> handler;
		
		public MouseClickListener(final TableRow<FXEntity> row, final Procedure1<FXEntity> handler) {
			this.row = row;
			this.handler = handler;
		}
		
		@Override
		public void handle(MouseEvent evt) {
			if (evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() == 2) {
				final FXEntity entity = row.getItem();
				
				if (entity != null)
					handler.apply(entity);
			}
		}
	}
	
	static private class MouseEnterListener implements EventHandler<MouseEvent> {
		
		private final TableRow<FXEntity> row;
		private final Procedure2<FXEntity, Node> handler;
		
		public MouseEnterListener(final TableRow<FXEntity> row, final Procedure2<FXEntity, Node> handler) {
			this.row = row;
			this.handler = handler;
		}
		
		@Override
		public void handle(MouseEvent evt) {
			final FXEntity entity = row.getItem();
			
			if (entity != null)
				handler.apply(entity, row);
		}
	}
}
