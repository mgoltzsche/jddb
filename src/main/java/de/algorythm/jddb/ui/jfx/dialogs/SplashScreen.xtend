package de.algorythm.jddb.ui.jfx.dialogs

import de.algorythm.jddb.bundle.Bundle
import java.util.Collection
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.HBox
import javafx.scene.layout.Region

class SplashScreen {
	
	val stage = new Stage
	
	new(Stage primaryStage, String title, Image splashLogo, Collection<Image> icons) {
		val box = new VBox(10)
		val progressBox = new HBox(10)
		val progressIndicator = new ProgressIndicator
		progressIndicator.setPrefSize(17, 17)
		progressIndicator.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
		
		progressBox.alignment = Pos.CENTER_LEFT;
		progressBox.children += progressIndicator
		progressBox.children += new Label(Bundle.instance.initializing)
		
		box.styleProperty.value = "-fx-border-color: #555555; -fx-border-width: 2px; -fx-border-radius: 10px";
		box.alignment = Pos.BASELINE_CENTER
		box.padding = new Insets(20)
		box.children += new ImageView(splashLogo)
		box.children += new Label(title)
		box.children += progressBox
		
		stage.initOwner(primaryStage)
		stage.initModality(Modality.APPLICATION_MODAL)
		stage.initStyle(StageStyle.UNDECORATED)
		stage.scene = new Scene(box)
		stage.title = title
		stage.icons += icons
		stage.show
		stage.centerOnScreen
	}
	
	def hide() {
		stage.hide
	}
}