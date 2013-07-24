package de.algorythm.jdoe.model.dao.impl.yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.yaml.snakeyaml.Yaml;

import de.algorythm.jdoe.model.dao.ISchemaDAO;
import de.algorythm.jdoe.model.meta.Schema;

public class SchemaDAO implements ISchemaDAO {

	private final Yaml yaml = new Yaml();
	private Schema schema;
	
	@Override
	public Schema load() throws IOException {
		try {
			schema = yaml.loadAs(new FileReader(new File("schema.yaml")), Schema.class);
			return schema;
		} catch(FileNotFoundException e) {
			return new Schema();
		}
	}

	@Override
	public void save(Schema schema) throws IOException {
		yaml.dump(schema, new FileWriter(new File("schema.yaml")));
		this.schema = schema;
	}

}
