package de.algorythm.jddb.bundle;

import javafx.scene.image.Image;

public class ImageBundle {
	
	static private final String IMG_RES = "/jddb-icons/";
	static private ImageBundle instance;
	
	static public ImageBundle getInstance() {
		if (instance == null)
			instance = new ImageBundle();
		
		return instance;
	}
	
	public final Image save;
	public final Image delete;
	public final Image minus;
	public final Image plus;
	
	private ImageBundle() {
		save = loadImage("save.png");
		delete = loadImage("delete.png");
		minus = loadImage("minus.png");
		plus = loadImage("plus.png");
	}
	
	private Image loadImage(final String fileName) {
		return new Image(getClass().getResource(IMG_RES + fileName).toExternalForm(), true);
	}
}
