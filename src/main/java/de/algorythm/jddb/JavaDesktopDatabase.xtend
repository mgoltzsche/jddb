package de.algorythm.jddb

import com.google.inject.Guice
import de.algorythm.jddb.ui.jfx.dialogs.JddbUncaughtExceptionHandler
import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage
import javax.inject.Inject
import org.slf4j.LoggerFactory

import static javafx.application.Platform.*
import de.algorythm.jddb.ui.jfx.dialogs.SplashScreen
import de.algorythm.jddb.ui.jfx.taskQueue.FXTaskQueue
import de.algorythm.jddb.taskQueue.ITaskPriority

class JavaDesktopDatabase extends Application {

	static val log = LoggerFactory.getLogger(typeof(JavaDesktopDatabase))
	
	@Inject JddbFacade facade
	@Inject extension FXTaskQueue

	def static main(String[] args) {
		launch(args)
	}

	override start(Stage primaryStage) {
		Thread.setDefaultUncaughtExceptionHandler(new JddbUncaughtExceptionHandler(primaryStage))
		
		val splashLogo = new Image('/jddb-icons/logo.png')
		val icon16 = new Image('/jddb-icons/logo16.png')
		val icon32 = new Image('/jddb-icons/logo32.png')
		val icon64 = new Image('/jddb-icons/logo64.png')
		val icons = newArrayList(icon16, icon32, icon64)
		val splash = new SplashScreen(primaryStage, 'Java Desktop Database', splashLogo, icons)
		
		new Thread [|
			try {
				val jddbModule = new JddbModule(primaryStage)
				val injector = Guice.createInjector(jddbModule)
				
				injector.injectMembers(this)
			} catch(Throwable e) {
				log.error('Error during module initialization: ' + e.message, e)
				System.exit(1)
			}
			
			runLater [|
				try {
					primaryStage.icons += icons
					
					facade.startApplication(primaryStage)
					
					runTask("hide-splash-screen", "Hide splash screen", ITaskPriority.LAST) [|
						runLater [|
							splash.hide
						]
					]
				} catch(Throwable e) {
					log.error('Error during application start: ' + e.message, e)
					System.exit(1)
				}
			]
		].start
	}

	override stop() {
		if (facade != null) {
			try {
				facade.stopApplication
				System.exit(0)
			} catch(Throwable e) {
				log.error('Error during application shutdown: ' + e.message, e)
			}
		}
		
		System.exit(1)
	}
}
