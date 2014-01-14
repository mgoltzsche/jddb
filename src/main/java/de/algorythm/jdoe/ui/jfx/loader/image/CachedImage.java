package de.algorythm.jdoe.ui.jfx.loader.image;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.image.Image;

public class CachedImage extends Image {

	private final ReadOnlyObjectProperty<Image> property;
	
	public CachedImage(final String url, final ReadOnlyObjectProperty<Image> property) {
		super(url, 300, 200, true, true);
		this.property = property;
	}
}
