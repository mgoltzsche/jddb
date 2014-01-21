package de.algorythm.jddb;

import javafx.application.Application;
import javafx.stage.Stage;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;

public class JavaDesktopDatabase extends Application {

	static private final Logger LOG = LoggerFactory.getLogger(JavaDesktopDatabase.class);
	
	@Inject
	private JavaDesktopDatabaseFacade facade;

	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		try {
			//Thread.currentThread().setDefaultUncaughtExceptionHandler(eh);
			Guice.createInjector(new JavaDesktopDatabaseModule())
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
		System.exit(0);
	}
}
