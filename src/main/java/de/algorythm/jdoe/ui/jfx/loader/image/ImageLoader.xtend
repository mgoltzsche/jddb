package de.algorythm.jdoe.ui.jfx.loader.image

import de.algorythm.jdoe.cache.ObjectCache
import de.algorythm.jdoe.cache.SoftCacheReferenceFactory
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.eclipse.xtext.xbase.lib.Functions.Function1
import org.slf4j.LoggerFactory

import static javafx.application.Platform.*
import de.algorythm.jdoe.taskQueue.ITaskPriority

class ImageLoader {

	static val LOG = LoggerFactory.getLogger(typeof(ImageLoader))
	static val INSTANCE = new ImageLoader
	
	def static getInstance() {
		INSTANCE
	}
	
	extension FXTaskQueue = new FXTaskQueue('image-loader-queue')
	val cache = new ObjectCache<ReadOnlyObjectProperty<Image>>('image-cache', new SoftCacheReferenceFactory<ReadOnlyObjectProperty<Image>>)
	val Function1<String, ReadOnlyObjectProperty<Image>> imageLoader = [
		val future = new SimpleObjectProperty<Image>
		
		runTask('''Load image «it»''', ITaskPriority.FIRST) [|
			loadImage(future)
		]
		
		future as ReadOnlyObjectProperty<Image>
	]
	
	private new() {
	}
	
	def bindImage(ImageView view, ReadOnlyStringProperty filePathProperty, ReadOnlyBooleanProperty visibleProperty) {
		val imageProperty = view.imageProperty
		view.cache = false
		imageProperty.updateImage(filePathProperty, visibleProperty)
		
		val ChangeListener<? super String> pathListener = [c,o,v|
			imageProperty.updateImage(filePathProperty, visibleProperty)
		]
		val ChangeListener<? super Boolean> visibleListener = [c,o,v|
			imageProperty.updateImage(filePathProperty, visibleProperty)
		]
		
		if (view.scene != null) {
			filePathProperty.addListener(pathListener)
			visibleProperty.addListener(visibleListener)
		}
		
		view.sceneProperty.addListener [c,o,v|
			if (v == null) {
				imageProperty.unbind
				imageProperty.value = null
				filePathProperty.removeListener(pathListener)
				visibleProperty.removeListener(visibleListener)
			} else {
				imageProperty.updateImage(filePathProperty, visibleProperty)
				filePathProperty.addListener(pathListener)
				visibleProperty.addListener(visibleListener)
			}
		]
	}
	
	def private void updateImage(ObjectProperty<Image> imageProperty, ReadOnlyStringProperty filePathProperty, ReadOnlyBooleanProperty visibleProperty) {
		imageProperty.unbind
		imageProperty.value = null
		
		if (!visibleProperty.value)
			return
		
		val filePath = filePathProperty.value
		
		if (filePath == null || filePath.empty)
			return;
		
		imageProperty.bind(cache.get(filePath, imageLoader))
	}
	
	def private loadImage(String filePath, SimpleObjectProperty<Image> imageProperty) {
		try {
			val image = new CachedImage('file:' + filePath, imageProperty)
			
			if (!image.error) {
				runLater [|
					imageProperty.value = image
				]
			}
		} catch(Exception e) {
			LOG.debug('Cannot load image ' + filePath, e)
		}
	}
}
