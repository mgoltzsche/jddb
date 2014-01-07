package de.algorythm.jdoe;

import javafx.application.Application;
import javafx.stage.Stage;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;

import de.algorythm.jdoe.cache.CacheCleanDaemon;

public class JavaDbObjectEditor extends Application {

	static private final Logger LOG = LoggerFactory.getLogger(JavaDbObjectEditor.class);
	
	@Inject
	private JavaDbObjectEditorFacade facade;

	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		try {
			Guice.createInjector(new JavaDbObjectEditorModule())
					.injectMembers(this);
	
			facade.startApplication(primaryStage);
		} catch(Throwable e) {
			LOG.error("Error during application start: " + e.getMessage(), e);
			System.exit(1);
		}
	}

	@Override
	public void stop() throws Exception {
		facade.stopApplication();
	}
}
