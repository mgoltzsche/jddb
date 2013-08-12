package de.algorythm.jdoe.ui.jfx.notification;

import de.algorythm.jdoe.controller.NotificationController
import de.algorythm.jdoe.taskQueue.ITaskNotifier.NotificationType
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.stage.Popup
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.util.Duration

public class Notification {

	static val TOP = 30
	static val WIDTH = 300
	static val HEIGHT = 30
	static val STEP = HEIGHT + 10
	static val LIFETIME = Duration.seconds(1.5)
	
	var String label
	val popup = new Popup
	var timeline = new Timeline(new KeyFrame(Duration.ZERO, 
									new KeyValue(popup.opacityProperty(), 1.0)
								),
								new KeyFrame(Duration.millis(500),
									new KeyValue(popup.opacityProperty(), 0.0)
								)
	)
	
	new(String label, NotificationType type, NotificationController ctrl) {
		this.label = label
		val hBox = new HBox
		val notificationLabel = new Label(label)
		val statusLabel = new Label(type.toString);
		
		hBox.prefWidth = WIDTH
		hBox.prefHeight = HEIGHT
		hBox.style = '-fx-background-color: eeeeee;'
		hBox.padding = new Insets(5, 5, 5, 5)
		hBox.spacing = 7
		hBox.children += statusLabel
		hBox.children += notificationLabel
		popup.content += hBox
		popup.x = Screen.screens.get(0).bounds.width / 2 - 150
		popup.y = TOP
		
		timeline.delay = LIFETIME
		timeline.setOnFinished [
			popup.hide
			ctrl.notificationExpired(this)
		]
	}
	
	def setPosition(int pos) {
		popup.y = TOP + pos * STEP
	}
	
	def show(Stage stage) {
		popup.show(stage)
		timeline.play
	}
}
