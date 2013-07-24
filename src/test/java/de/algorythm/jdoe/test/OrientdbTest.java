package de.algorythm.jdoe.test;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class OrientdbTest {

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
		final Vertex vertex = db.addVertex(UUID.randomUUID().toString());
		
		vertex.setProperty("name", "testvertex");
	}
	
	@Test
	public void listData() {
		
		for (Vertex vertex : db.getVertices())
			System.out.println(vertex.getProperty("name"));
	}
}
