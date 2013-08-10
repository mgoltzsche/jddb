package de.algorythm.jdoe;

import com.google.inject.AbstractModule;

import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.dao.impl.orientdb.DAO;

public class JavaDbObjectEditorModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(IDAO.class).to(DAO.class);
	}
}