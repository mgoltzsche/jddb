package de.algorythm.jddb.model.dao;

import java.io.IOException;

import de.algorythm.jddb.model.meta.Schema;

public interface ISchemaDAO {

	Schema load() throws IOException;
	void save(Schema schema) throws IOException;
}
