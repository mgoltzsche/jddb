package de.algorythm.jdoe.model.dao;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.meta.MEntityType;

public interface IDAO<V extends IEntity<P,REF>, P extends IPropertyValue<?,REF>, REF extends IEntityReference> {
	
	boolean isOpened();
	void open(File dbFile) throws IOException;
	void close() throws IOException;
	void addObserver(IObserver<V,P,REF> observer);
	void removeObserver(IObserver<V,P,REF> observer);
	ISchema getSchema();
	void updateSchemaTypes(Collection<MEntityType> types) throws IOException;
	Set<V> list(MEntityType type);
	Set<V> list(MEntityType type, String search);
	V createNewEntity(MEntityType type);
	V find(IEntityReference entityRef);
	V find(String id);
	boolean exists(IEntityReference entityRef);
	void transaction(Procedure1<IDAOTransactionContext<V,P,REF>> transaction);
	void rebuildIndex();
}
