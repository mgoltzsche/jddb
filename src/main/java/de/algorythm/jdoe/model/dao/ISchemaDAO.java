package de.algorythm.jdoe.model.dao;

import java.io.IOException;

import de.algorythm.jdoe.model.meta.Schema;

public interface ISchemaDAO {

	Schema load() throws IOException;
	void save(Schema schema) throws IOException;
}
