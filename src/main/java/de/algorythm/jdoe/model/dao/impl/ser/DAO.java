package de.algorythm.jdoe.model.dao.impl.ser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

import javax.inject.Singleton;

import de.algorythm.jdoe.model.dao.IDAO;
import de.algorythm.jdoe.model.dao.IObserver;
import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.meta.EntityType;
import de.algorythm.jdoe.model.meta.Schema;

@Singleton
public class DAO implements IDAO {

	private DatabaseContainer db;
	
	@Override
	public void open() throws IOException {
		try {
			FileInputStream fileIn = new FileInputStream(new File("schema.ser"));
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			
			try {
				db = (DatabaseContainer) objIn.readObject();
			} catch (ClassNotFoundException e) {
				throw new IOException("Invalid schema format", e);
			} finally {
				objIn.close();
				fileIn.close();
			}
		} catch(FileNotFoundException e) {
			db = new DatabaseContainer();
		}
	}

	@Override
	public void close() throws IOException {
		FileOutputStream fileOut = new FileOutputStream(new File("schema.ser"));
		ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
		
		try {
			objOut.writeObject(db);
		} finally {
			objOut.close();
			fileOut.close();
		}
	}

	@Override
	public Schema getSchema() {
		return db.getSchema();
	}

	@Override
	public void setSchema(Schema schema) { // TODO: remove
		//db.setSchema(schema);
	}

	@Override
	public void save(IEntity entity) {
		db.getEntities().add(entity);
	}

	@Override
	public void delete(IEntity entity) {
		db.getEntities().remove(entity);
	}

	@Override
	public IEntity createEntity(EntityType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IEntity> list(EntityType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addObserver(IObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeObserver(IObserver observer) {
		// TODO Auto-generated method stub
		
	}
}
