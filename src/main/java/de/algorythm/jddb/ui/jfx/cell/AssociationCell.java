package de.algorythm.jddb.ui.jfx.cell;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import de.algorythm.jddb.JddbFacade;
import de.algorythm.jddb.bundle.ImageBundle;
import de.algorythm.jddb.ui.jfx.model.FXEntityReference;

public class AssociationCell extends EntityReferenceCell {

	static public class Factory implements Callback<ListView<FXEntityReference>, ListCell<FXEntityReference>> {

		private final JddbFacade facade;
		private final Procedure1<FXEntityReference> onRemove;
		
		public Factory(final JddbFacade facade, final Procedure1<FXEntityReference> onRemove) {
			this.facade = facade;
			this.onRemove = onRemove;
		}
		
		@Override
		public ListCell<FXEntityReference> call(final ListView<FXEntityReference> view) {
			return new AssociationCell(facade, onRemove);
		}
	};
	
	
	private final HBox hBox = new HBox();
	private final Label label = new Label();
	private final Region spacer = new Region();
	private final Button removeButton = new Button("remove");
	
	public AssociationCell(final JddbFacade facade, final Procedure1<FXEntityReference> onRemove) {
		super(facade);
		
		HBox.setHgrow(spacer, Priority.ALWAYS);
		hBox.setAlignment(Pos.CENTER_LEFT);
		
		final ObservableList<Node> hBoxChildren = hBox.getChildren();
		
		hBoxChildren.add(label);
		hBoxChildren.add(spacer);
		hBoxChildren.add(removeButton);
		
		removeButton.setGraphic(new ImageView(ImageBundle.getInstance().minus));
		removeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent evt) {
				final FXEntityReference entityRef = getItem();
				onRemove.apply(entityRef);
				getListView().getItems().remove(entityRef);
			}
		});
	}
	
	@Override
	public void updateItem(FXEntityReference entity, boolean empty) {
		super.updateItem(entity, empty);
		
		if (!empty)
			setGraphic(hBox);
	}
}
