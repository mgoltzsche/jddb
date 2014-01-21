package de.algorythm.jddb.ui.jfx.loader.image;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.image.Image;

public class CachedImage extends Image {

	private final ReadOnlyObjectProperty<Image> containedProperty;
	
	public CachedImage(final String url, final ReadOnlyObjectProperty<Image> containedProperty) {
		super(url, 300, 200, true, true);
		this.containedProperty = containedProperty;
	}
	
	public ReadOnlyObjectProperty<Image> getContainedProperty() {
		return containedProperty;
	}
}
