package de.algorythm.jdoe.model.dao.impl.ser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.algorythm.jdoe.model.dao.ISchemaDAO;
import de.algorythm.jdoe.model.meta.Schema;

public class SchemaDAO implements ISchemaDAO {

	@Override
	public Schema load() throws IOException {
		try {
			FileInputStream fileIn = new FileInputStream(new File("schema.ser"));
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			
			try {
				return (Schema) objIn.readObject();
			} catch (ClassNotFoundException e) {
				throw new IOException("Invalid schema format", e);
			} finally {
				objIn.close();
				fileIn.close();
			}
		} catch(FileNotFoundException e) {
			return new Schema();
		}
	}

	@Override
	public void save(Schema schema) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(new File("schema.ser"));
		ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
		
		try {
			objOut.writeObject(schema);
		} finally {
			objOut.close();
			fileOut.close();
		}
	}
}
