package de.algorythm.jdoe;

import java.io.File;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import de.algorythm.jdoe.bundle.Bundle;
import de.algorythm.jdoe.cache.ObjectCache;
import de.algorythm.jdoe.cache.SoftCacheReferenceFactory;
import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.dao.impl.neo4j.Neo4jDbDAO;
import de.algorythm.jdoe.ui.jfx.loader.image.ImageLoader;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.factory.FXModelFactory;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;
import de.algorythm.jdoe.ui.jfx.taskQueue.FXTaskQueue;
import de.algorythm.jdoe.ui.jfx.util.EntityEditorViewRegistry;
import de.algorythm.jdoe.ui.jfx.util.IEntityEditorManager;

public class JavaDbObjectEditorModule extends AbstractModule {
	
	@Override
	protected void configure() {
		final FXModelFactory modelFactory = new FXModelFactory();
		final String prefPath = System.getProperty("user.home") + File.separator + ".jdoe";
		final File preferencesDirectory = new File(prefPath);
		
		final IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao = new Neo4jDbDAO<>(modelFactory);
		
		bind(Config.class).toInstance(new Config(preferencesDirectory));
		bind(FXTaskQueue.class).toInstance(new FXTaskQueue("entity-loader-queue"));
		bind(IEntityEditorManager.class).to(EntityEditorViewRegistry.class);
		bind(Bundle.class).toInstance(Bundle.getInstance());
		bind(new TypeLiteral<IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference>>() {}).toInstance(dao);
		bind(ImageLoader.class).toInstance(ImageLoader.getInstance());
		
		modelFactory.init(dao, new ObjectCache<FXEntity>("entity-cache", new SoftCacheReferenceFactory<FXEntity>()));
	}
}