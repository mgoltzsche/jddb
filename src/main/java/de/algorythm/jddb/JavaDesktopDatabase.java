package de.algorythm.jddb;

import java.lang.Thread.UncaughtExceptionHandler;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.algorythm.jddb.taskQueue.ITaskQueueExceptionHandler;
import de.algorythm.jddb.ui.jfx.dialogs.JddbTaskFailureHandler;
import de.algorythm.jddb.ui.jfx.dialogs.JddbUncaughtExceptionHandler;

public class JavaDesktopDatabase extends Application {

	static private final Logger log = LoggerFactory.getLogger(JavaDesktopDatabase.class);
	
	@Inject
	private JddbFacade facade;

	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		ObservableList<Image> icons = primaryStage.getIcons();
		
		icons.addAll(new Image("/jddb-icons/logo16.png"), new Image("/jddb-icons/logo32.png"), new Image("/jddb-icons/logo.png"));
		
		try {
			final UncaughtExceptionHandler uncaughtExceptionHandler = new JddbUncaughtExceptionHandler(primaryStage);
			
			Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
			//Thread.currentThread().setUncaughtExceptionHandler(uncaughtExceptionHandler);
			
			final ITaskQueueExceptionHandler failureHandler = new JddbTaskFailureHandler(primaryStage);
			final JddbModule jddbModule = new JddbModule(failureHandler);
			final Injector injector = Guice.createInjector(jddbModule);
			
			injector.injectMembers(this);
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
