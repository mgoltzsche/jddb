package de.algorythm.jdoe;

import javafx.application.Application;
import javafx.stage.Stage;

import javax.inject.Inject;

import com.google.inject.Guice;

public class JavaDbObjectEditor extends Application {

	@Inject
	private JavaDbObjectEditorFacade facade;

	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		Guice.createInjector(new JavaDbObjectEditorModule())
				.injectMembers(this);

		facade.startApplication(primaryStage);
	}

	@Override
	public void stop() throws Exception {
		facade.stopApplication();
	}
}
