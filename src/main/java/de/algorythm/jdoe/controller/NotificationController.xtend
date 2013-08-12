package de.algorythm.jdoe.controller

import de.algorythm.jdoe.taskQueue.ITaskNotifier
import de.algorythm.jdoe.ui.jfx.notification.Notification
import java.util.LinkedHashSet
import javafx.application.Platform
import javafx.stage.Stage
import de.algorythm.jdoe.taskQueue.ITaskNotifier.NotificationType

class NotificationController implements ITaskNotifier {
	
	var Stage stage
	val notifications = new LinkedHashSet<Notification>
	
	new(Stage stage) {
		this.stage = stage
	}
	
	override showNotification(String label, NotificationType type) {
		Platform.runLater [|
			val notification = new Notification(label, type, this)
			
			notifications += notification
			
			movePopups
			
			notification.show(stage)
		]
	}
	
	def notificationExpired(Notification notification) {
		notifications.remove(notification)
		movePopups
	}
	
	def private void movePopups() {
		var i = 0
		
		for (notification : notifications) {
			notification.position = i
			i = i + 1
		}
	}
}