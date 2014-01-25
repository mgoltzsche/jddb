package de.algorythm.jddb;

import java.io.File;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import de.algorythm.jddb.bundle.Bundle;
import de.algorythm.jddb.cache.IObjectCache;
import de.algorythm.jddb.cache.ObjectCache;
import de.algorythm.jddb.cache.WeakCacheReferenceFactory;
import de.algorythm.jddb.model.dao.IDAO;
import de.algorythm.jddb.model.dao.impl.neo4j.Neo4jDbDAO;
import de.algorythm.jddb.taskQueue.ITaskQueueExceptionHandler;
import de.algorythm.jddb.ui.jfx.loader.image.ImageLoader;
import de.algorythm.jddb.ui.jfx.model.FXEntity;
import de.algorythm.jddb.ui.jfx.model.FXEntityReference;
import de.algorythm.jddb.ui.jfx.model.factory.FXModelFactory;
import de.algorythm.jddb.ui.jfx.model.propertyValue.IFXPropertyValue;
import de.algorythm.jddb.ui.jfx.taskQueue.FXTaskQueue;
import de.algorythm.jddb.ui.jfx.util.EntityEditorViewRegistry;
import de.algorythm.jddb.ui.jfx.util.IEntityEditorManager;

public class JddbModule extends AbstractModule {
	
	private final ITaskQueueExceptionHandler taskFailureHandler;
	
	public JddbModule(final ITaskQueueExceptionHandler taskFailureHandler) {
		this.taskFailureHandler = taskFailureHandler;
	}
	
	@Override
	protected void configure() {
		final FXModelFactory modelFactory = new FXModelFactory();
		final IObjectCache<FXEntity> entityCache = new ObjectCache<>("entity-cache", new WeakCacheReferenceFactory<FXEntity>());
		final String prefPath = System.getProperty("user.home") + File.separator + ".jdoe";
		final File preferencesDirectory = new File(prefPath);
		final IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao = new Neo4jDbDAO<>(modelFactory);
		
		bind(Config.class).toInstance(new Config(preferencesDirectory));
		bind(FXTaskQueue.class).toInstance(new FXTaskQueue("entity-loader-queue", taskFailureHandler));
		bind(IEntityEditorManager.class).to(EntityEditorViewRegistry.class);
		bind(Bundle.class).toInstance(Bundle.getInstance());
		bind(new TypeLiteral<IObjectCache<FXEntity>>() {}).toInstance(entityCache);
		bind(new TypeLiteral<IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference>>() {}).toInstance(dao);
		bind(ImageLoader.class).toInstance(ImageLoader.INSTANCE);
		
		modelFactory.init(dao, entityCache);
	}
}