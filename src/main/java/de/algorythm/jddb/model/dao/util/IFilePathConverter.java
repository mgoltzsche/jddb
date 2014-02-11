package de.algorythm.jddb.model.dao.util;

import java.io.File;

public interface IFilePathConverter {
	
	String toAbstractRelativePath(File file);
	String toAbsolutePath(String filePath);
}