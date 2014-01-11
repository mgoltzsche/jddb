package de.algorythm.jdoe.ui.jfx.util;

import java.io.File;

import javafx.scene.image.Image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.jdoe.cache.IObjectCache;
import de.algorythm.jdoe.cache.ObjectCache;

public class ImageLoader {

	static private final Logger LOG = LoggerFactory.getLogger(ImageLoader.class);
	static private final ImageLoader INSTANCE = new ImageLoader();
	
	static public ImageLoader getInstance() {
		return INSTANCE;
	}
	
	
	private IObjectCache<Image> cache = new ObjectCache<>();
	
	private ImageLoader() {
	}
	
	public Image loadImage(final String filePath) {
		Image image = cache.get(filePath);
		
		if (image == null && new File(filePath).exists()) {
			try {
				image = new Image("file:" + filePath, true);
				cache.put(filePath, image);
			} catch(Exception e) {
				LOG.debug("Cannot load image " + filePath, e);
			}
		}
		
		return image;
	}
}
