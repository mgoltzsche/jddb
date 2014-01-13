package de.algorythm.jdoe.ui.jfx.loader.image

import de.algorythm.jdoe.cache.ObjectCache
import de.algorythm.jdoe.cache.WeakCacheReferenceFactory
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.value.ChangeListener
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.eclipse.xtext.xbase.lib.Functions.Function1
import org.slf4j.LoggerFactory

class ImageLoader {

	static val LOG = LoggerFactory.getLogger(typeof(ImageLoader))
	static val INSTANCE = new ImageLoader
	
	def static getInstance() {
		INSTANCE
	}
	
	val cache = new ObjectCache<Image>('image-cache', new WeakCacheReferenceFactory<Image>)
	val Function1<String, Image> imageLoader = [loadImage]
	
	private new() {
	}
	
	def bindImage(ImageView view, ReadOnlyStringProperty filePathProperty, ReadOnlyBooleanProperty visibleProperty) {
		view.cache = false
		view.updateImage(filePathProperty, visibleProperty)
		
		val ChangeListener<? super String> pathListener = [c,o,v|
			view.updateImage(filePathProperty, visibleProperty)
		]
		val ChangeListener<? super Boolean> visibleListener = [c,o,v|
			view.updateImage(filePathProperty, visibleProperty)
		]
		
		if (view.scene != null) {
			filePathProperty.addListener(pathListener)
			visibleProperty.addListener(visibleListener)
		}
		
		view.sceneProperty.addListener [c,o,v|
			if (v == null) {
				view.image = null
				filePathProperty.removeListener(pathListener)
				visibleProperty.removeListener(visibleListener)
			} else {
				view.updateImage(filePathProperty, visibleProperty)
				filePathProperty.addListener(pathListener)
				visibleProperty.addListener(visibleListener)
			}
		]
	}
	
	def private void updateImage(ImageView view, ReadOnlyStringProperty filePathProperty, ReadOnlyBooleanProperty visibleProperty) {
		view.image = null
		
		if (!visibleProperty.value)
			return
		
		val filePath = filePathProperty.value
		
		if (filePath == null || filePath.empty)
			return;
		
		view.image = cache.get(filePath, imageLoader)
	}
	
	def private loadImage(String filePath) {
		System.gc();
		var Image image
		try {
			image = new Image('file:' + filePath, 300, 200, true, true, true)
			image.errorProperty.addListener [c,o,error|
				if (error)
					LOG.debug('Failed to load image: ' + filePath)
			]
		} catch(Exception e) {
			LOG.debug('Cannot load image ' + filePath, e)
		}
		image
	}
}
