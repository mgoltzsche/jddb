package de.algorythm.jddb;

import java.io.File;

import javafx.stage.Stage;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import de.algorythm.jddb.bundle.Bundle;
import de.algorythm.jddb.cache.IObjectCache;
import de.algorythm.jddb.cache.ObjectCache;
import de.algorythm.jddb.cache.WeakCacheReferenceFactory;
import de.algorythm.jddb.model.dao.IDAO;
import de.algorythm.jddb.model.dao.impl.neo4j.Neo4jDbDAO;
import de.algorythm.jddb.model.dao.util.FilePathManager;
import de.algorythm.jddb.model.dao.util.IFilePathConverter;
import de.algorythm.jddb.taskQueue.ITaskQueueExceptionHandler;
import de.algorythm.jddb.ui.jfx.dialogs.ConfirmDialog;
import de.algorythm.jddb.ui.jfx.dialogs.JddbTaskFailureHandler;
import de.algorythm.jddb.ui.jfx.loader.image.ImageLoader;
import de.algorythm.jddb.ui.jfx.model.FXEntity;
import de.algorythm.jddb.ui.jfx.model.FXEntityReference;
import de.algorythm.jddb.ui.jfx.model.factory.FXModelFactory;
import de.algorythm.jddb.ui.jfx.model.propertyValue.IFXPropertyValue;
import de.algorythm.jddb.ui.jfx.taskQueue.FXTaskQueue;
import de.algorythm.jddb.ui.jfx.util.EntityEditorViewRegistry;
import de.algorythm.jddb.ui.jfx.util.IEntityEditorManager;
import de.algorythm.jddb.ui.jfx.util.OpenFileUtil;

public class JddbModule extends AbstractModule {
	
	private final Stage primaryStage;
	
	public JddbModule(final Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
	@Override
	protected void configure() {
		try {
			final String userHome = System.getProperty("user.home");
			final String prefPath = userHome + File.separator + ".jddb";
			final File preferencesDirectory = new File(prefPath);
			final File appStateFile = new File(prefPath + File.separator + "last.session");
			final FilePathManager pathManager = new FilePathManager(new File(preferencesDirectory.getAbsolutePath() + File.separator + "root-directories.cfg"), new File(userHome));
			final FXModelFactory modelFactory = new FXModelFactory();
			final IObjectCache<FXEntity> entityCache = new ObjectCache<>("entity-cache", new WeakCacheReferenceFactory<FXEntity>());
			final IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao = new Neo4jDbDAO<>(modelFactory);
			final ITaskQueueExceptionHandler taskFailureHandler = new JddbTaskFailureHandler(primaryStage);
			final ApplicationStateManager appStateManager = new ApplicationStateManager(appStateFile);
			
			ImageLoader.INSTANCE.setFilePathConverter(pathManager);
			OpenFileUtil.setFilePathConverter(pathManager);
			
			bind(ApplicationStateManager.class).toInstance(appStateManager);
			bind(FilePathManager.class).toInstance(pathManager);
			bind(IFilePathConverter.class).toInstance(pathManager);
			bind(FXTaskQueue.class).toInstance(new FXTaskQueue("entity-loader-queue", taskFailureHandler));
			bind(IEntityEditorManager.class).to(EntityEditorViewRegistry.class);
			bind(Bundle.class).toInstance(Bundle.getInstance());
			bind(new TypeLiteral<IObjectCache<FXEntity>>() {}).toInstance(entityCache);
			bind(new TypeLiteral<IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference>>() {}).toInstance(dao);
			bind(ConfirmDialog.class).toInstance(new ConfirmDialog(primaryStage));
			bind(ImageLoader.class).toInstance(ImageLoader.INSTANCE);
			
			modelFactory.init(dao, entityCache);
		} catch(Throwable e) {
			throw new RuntimeException("Error during JddbModule configuration", e);
		}
	}
}