package de.algorythm.jdoe;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import de.algorythm.jdoe.bundle.Bundle;
import de.algorythm.jdoe.cache.ObjectCache;
import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.dao.impl.orientdb.DAO;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.factory.FXModelFactory;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValue;
import de.algorythm.jdoe.ui.jfx.util.IEntityEditorManager;
import de.algorythm.jdoe.ui.jfx.util.ViewRegistry;

public class JavaDbObjectEditorModule extends AbstractModule {
	
	@Override
	protected void configure() {
		final FXModelFactory modelFactory = new FXModelFactory();
		final IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference> dao = new DAO<>(modelFactory);
		
		bind(IEntityEditorManager.class).to(ViewRegistry.class);
		bind(Bundle.class).toInstance(Bundle.getInstance());
		bind(new TypeLiteral<IDAO<FXEntity,IFXPropertyValue<?>,FXEntityReference>>() {}).toInstance(dao);
		
		modelFactory.init(dao, new ObjectCache<FXEntity>());
	}
}