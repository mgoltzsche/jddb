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
	
	public final Image logo;
	public final Image warn;
	public final Image stop;
	public final Image search;
	public final Image create;
	public final Image edit;
	public final Image save;
	public final Image delete;
	public final Image add;
	public final Image details;
	public final Image chooseFile;
	
	private ImageBundle() {
		logo = loadImage("logo.png");
		warn = loadImage("warning_32.png");
		stop = loadImage("stop_32.png");
		search = loadImage("search_16.png");
		create = loadImage("document_16.png");
		edit = loadImage("pencil_16.png");
		save = loadImage("tick_16.png");
		delete = loadImage("delete_16.png");
		add = loadImage("plus_16.png");
		details = loadImage("bubble_16.png");
		chooseFile = loadImage("folder_16.png");
	}
	
	private Image loadImage(final String fileName) {
		return new Image(getClass().getResource(IMG_RES + fileName).toExternalForm(), true);
	}
}
