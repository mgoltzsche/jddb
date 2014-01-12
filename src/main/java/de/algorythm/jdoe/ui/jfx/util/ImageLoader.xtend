package de.algorythm.jdoe.ui.jfx.util

import de.algorythm.jdoe.cache.ObjectCache
import java.io.File
import javafx.scene.image.Image
import org.slf4j.LoggerFactory
import javafx.scene.image.ImageView
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.ReadOnlyBooleanProperty

class ImageLoader {

	static val LOG = LoggerFactory.getLogger(typeof(ImageLoader))
	static val INSTANCE = new ImageLoader
	
	def static getInstance() {
		INSTANCE
	}
	
	
	val cache = new ObjectCache<Image>
	
	private new() {
	}
	
	def bindImage(ImageView view, ReadOnlyStringProperty filePathProperty, ReadOnlyBooleanProperty visibleProperty) {
		view.loadImage(filePathProperty, visibleProperty)
		
		filePathProperty.addListener [c,o,v|
			view.loadImage(filePathProperty, visibleProperty)
		]
		
		visibleProperty.addListener [c,o,v|
			view.loadImage(filePathProperty, visibleProperty)
		]
	}
	
	def private void loadImage(ImageView view, ReadOnlyStringProperty filePathProperty, ReadOnlyBooleanProperty visibleProperty) {
		view.image = null
		
		if (!visibleProperty.value)
			return
		
		val filePath = filePathProperty.value
		
		if (filePath == null || filePath.empty)
			return
		
		var image = cache.get(filePath)
		
		if (image == null && new File(filePath).exists) {
			try {
				System.gc();
				image = new Image('file:' + filePath, 200, 100, true, true, true)
				cache.put(filePath, image)
				System.gc();
			} catch(Exception e) {
				LOG.debug('Cannot load image ' + filePath, e)
			}
		}
		
		view.image = image
	}
}
