package de.algorythm.jdoe;

import com.google.inject.AbstractModule;

import de.algorythm.jdoe.bundle.Bundle;
import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.dao.IModelFactory;
import de.algorythm.jdoe.model.dao.impl.orientdb.DAO;
import de.algorythm.jdoe.ui.jfx.model.FXEntity;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.FXPropertyValue;
import de.algorythm.jdoe.ui.jfx.model.factory.FXModelFactory;
import de.algorythm.jdoe.ui.jfx.util.IEntityEditorManager;
import de.algorythm.jdoe.ui.jfx.util.ViewRegistry;

public class JavaDbObjectEditorModule extends AbstractModule {
	
	@Override
	protected void configure() {
		final IModelFactory<FXEntity, FXEntityReference, FXPropertyValue<?>> modelFactory = new FXModelFactory();
		
		bind(IDAO.class).to(DAO.class);
		bind(IEntityEditorManager.class).to(ViewRegistry.class);
		bind(Bundle.class).toInstance(Bundle.getInstance());
		bind(IDAO.class).toInstance(new DAO<>(modelFactory));
	}
}