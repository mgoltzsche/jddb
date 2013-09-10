package de.algorythm.jdoe.ui.jfx.cell;

import java.util.HashMap;

import de.algorythm.jdoe.model.meta.TextAlignment;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class PropertyValueCell extends TableCell<FXEntity, IFXPropertyValue<?>>{

	static public class Factory implements Callback<TableColumn<FXEntity, IFXPropertyValue<?>>, TableCell<FXEntity, IFXPropertyValue<?>>> {

		@Override
		public TableCell<FXEntity, IFXPropertyValue<?>> call(
				final TableColumn<FXEntity, IFXPropertyValue<?>> column) {
			return new PropertyValueCell();
		}
	}
	
	static private final String EMPTY = "";
	static private final HashMap<TextAlignment, Pos> alignmentMap = new HashMap<>();
	static {
		alignmentMap.put(TextAlignment.LEFT, Pos.CENTER_LEFT);
		alignmentMap.put(TextAlignment.CENTER, Pos.CENTER);
		alignmentMap.put(TextAlignment.RIGHT, Pos.CENTER_RIGHT);
	}
	
	@Override
	public void updateItem(final IFXPropertyValue<?> item, final boolean empty) {
		if (empty) {
			textProperty().unbind();
			setText(EMPTY);
		} else {
			final Pos alignment = alignmentMap.get(item.getProperty().getType().getTextAlignment());
			
			setAlignment(alignment);
			textProperty().bind(item.labelProperty());
		}
	}
}