package de.algorythm.jdoe;

import javafx.application.Application;
import javafx.stage.Stage;

import javax.inject.Inject;

import com.google.inject.Guice;

import de.algorythm.jdoe.controller.codegen.InMemoryCompiler;

public class JavaDbObjectEditor extends Application {

	@Inject private JavaDbObjectEditorFacade facade;

	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		new InMemoryCompiler().compile();
		
		Guice.createInjector(new JavaDbObjectEditorModule())
				.injectMembers(this);

		facade.startApplication(stage);
	}
	
	@Override
	public void stop() throws Exception {
		System.out.println("db closed");
		facade.stopApplication();
	}
}
