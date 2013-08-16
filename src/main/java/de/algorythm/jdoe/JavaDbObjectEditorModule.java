package de.algorythm.jdoe;

import com.google.inject.AbstractModule;

import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.dao.impl.orientdb.DAO;
import de.algorythm.jdoe.ui.jfx.util.IEntityEditorManager;
import de.algorythm.jdoe.ui.jfx.util.ViewRegistry;

public class JavaDbObjectEditorModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(IDAO.class).to(DAO.class);
		bind(IEntityEditorManager.class).to(ViewRegistry.class);
	}
}