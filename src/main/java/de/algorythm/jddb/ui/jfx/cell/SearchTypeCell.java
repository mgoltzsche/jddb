package de.algorythm.jddb.ui.jfx.cell;

import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import de.algorythm.jddb.bundle.ImageBundle;
import de.algorythm.jddb.model.meta.MEntityType;

public class SearchTypeCell extends ListCell<MEntityType> {
	
	private final ImageView searchIcon = new ImageView(ImageBundle.getInstance().search);
	
	@Override
	public void updateItem(final MEntityType type, final boolean empty) {
		super.updateItem(type, empty);
		
		if (empty)
			setText(null);
		else
			setText(type.getLabel());
		
		setGraphic(searchIcon);
	}
}
