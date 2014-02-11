package de.algorythm.jddb.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.algorythm.jddb.model.dao.util.FilePathManager;
import de.algorythm.jddb.model.dao.util.IFilePathConverter;

public class TestFilePathConverter {

	static private final File TEST_DIR = new File("tmp-testfilepathconverter");
	
	private String rootPath;
	private IFilePathConverter converter;
	
	@Before
	public void setUp() throws IOException {
		rootPath = TEST_DIR.getAbsolutePath();
		converter = new FilePathManager(new File(rootPath + File.separator + "root-directories.cfg"), new File(rootPath));
	}
	
	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(TEST_DIR);
	}
	
	@Test
	public void testToAbstractRelativePath() {
		final String expectedRelativePath = "mydir" + File.separator + "myFile.txt";
		final String absolutePath = rootPath + File.separator + expectedRelativePath;
		final String relativePath = converter.toAbstractRelativePath(new File(absolutePath));
		
		Assert.assertEquals(expectedRelativePath, relativePath);
	}
	
	@Test
	public void testToAbsolutePath() throws IOException {
		final String relativePath = "mydir" + File.separator + "myFile.txt";
		final String expectedAbsolutePath = rootPath + File.separator + relativePath;
		
		FileUtils.writeStringToFile(new File(expectedAbsolutePath), "asdf");
		
		final String absolutePath = converter.toAbsolutePath(relativePath);
		
		Assert.assertEquals(expectedAbsolutePath, absolutePath);
	}
	
	@Test
	public void testRootPathToAbstractRelativePath() {
		final File expectedFile = new File(rootPath);
		final String relativePath = converter.toAbstractRelativePath(expectedFile);
		
		Assert.assertEquals(expectedFile.getAbsolutePath(), relativePath);
	}
	
	@Test
	public void testRootPathToAbsolutePath() throws IOException {
		final String absolutePath = converter.toAbsolutePath(rootPath);
		
		Assert.assertEquals(rootPath, absolutePath);
	} 
}
