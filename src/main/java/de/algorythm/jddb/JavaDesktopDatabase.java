package de.algorythm.jddb;

import javafx.application.Application;
import javafx.stage.Stage;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;

import de.algorythm.jddb.taskQueue.ITaskQueueExceptionHandler;

public class JavaDesktopDatabase extends Application {

	static private final Logger log = LoggerFactory.getLogger(JavaDesktopDatabase.class);
	
	@Inject
	private JddbFacade facade;

	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		try {
			final ITaskQueueExceptionHandler failureHandler = new JddbTaskFailureHandler(primaryStage);
			Thread.setDefaultUncaughtExceptionHandler(new JddbUncaughtExceptionHandler(primaryStage));
			Guice.createInjector(new JddbModule(failureHandler)).injectMembers(this);

			facade.startApplication(primaryStage);
		} catch(Throwable e) {
			log.error("Error during application start: " + e.getMessage(), e);
			System.exit(1);
		}
	}

	@Override
	public void stop() throws Exception {
		facade.stopApplication();
		System.exit(0);
	}
}
