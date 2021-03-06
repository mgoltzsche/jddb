package de.algorythm.jddb.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class OrientdbTest {

	static private final Logger log = LoggerFactory.getLogger(OrientdbTest.class);
	
	private OrientGraph db;
	
	@Before
	public void setUp() {
		db = new OrientGraph("local:test.db");
	}
	
	@After
	public void tearDown() {
		db.shutdown();
	}
	
	@Test
	public void saveShouldPersistData() {
		//Index<Vertex> index = db.getIndex("asdf", Vertex.class);
		
		
		final Vertex vertex;
		
		try {
			vertex = db.addVertex(null);
		
			vertex.setProperty("name", "testvertex");
			
			db.commit();
		} catch(Throwable e) {
			db.rollback();
			throw e;
		}
		
		log.info("vertex id after 1st transaction: " + vertex);
		
		try {
			vertex.setProperty("name", "testvertexwithchangedtitle");
			
			db.commit();
		} catch(Throwable e) {
			db.rollback();
			throw e;
		}
		
		log.info("vertex id after 2nd transaction: " + vertex);
	}
	
	@Test
	public void listData() {
		for (Vertex vertex : db.getVertices())
			log.info(vertex.getProperty("name"));
	}
}
