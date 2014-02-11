package de.algorythm.jddb.model.dao.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class FilePathManager implements IFilePathConverter {
	
	private final File rootDirectoryConfigFile;
	private final LinkedList<File> rootDirectories = new LinkedList<>();
	
	public FilePathManager(final File rootDirectoryConfigFile, final File defaultRootDirectory) throws IOException {
		this.rootDirectoryConfigFile = rootDirectoryConfigFile;
		loadBasePathes(defaultRootDirectory);
	}
	
	@SuppressWarnings("unchecked")
	private void loadBasePathes(final File defaultRootDirectory) throws IOException {
		if (rootDirectoryConfigFile.exists()) {
			for (String basePathStr : (List<String>) FileUtils.readLines(rootDirectoryConfigFile, "UTF-8")) {
				if (!basePathStr.isEmpty()) {
					final File basePath = new File(basePathStr);
					
					if (basePath.exists())
						rootDirectories.add(basePath);
				}
			}
		}
		
		if (rootDirectories.isEmpty())
			rootDirectories.add(defaultRootDirectory);
	}
	
	@Override
	public String toAbstractRelativePath(File file) {
		final String absolutePath = file.getAbsolutePath();
		
		for (File rootDir : rootDirectories) {
			final String absoluteRootPath = rootDir.getAbsolutePath() + File.separator;
			
			if (absolutePath.startsWith(absoluteRootPath))
				return toPlatformIndependentPath(absolutePath.substring(absoluteRootPath.length()));
		}
		
		return toPlatformIndependentPath(absolutePath);
	}
	
	@Override
	public String toAbsolutePath(String filePath) {
		filePath = filePath.replace('/', File.separatorChar);
		
		for (File rootDir : rootDirectories) {
			final String path = rootDir.getAbsolutePath() + File.separator + filePath;
			final File file = new File(path);
			
			if (file.exists())
				return path;
		}
		
		return filePath;
	}
	
	public List<File> getRootDirectories() {
		return Collections.unmodifiableList(rootDirectories);
	}
	
	public synchronized void setRootDirectories(final List<File> basePathes) throws IOException {
		this.rootDirectories.clear();
		this.rootDirectories.addAll(basePathes);
		
		final StringBuilder sb = new StringBuilder();
		
		for (File basePath : basePathes)
			sb.append(basePath).append("\n");
		
		FileUtils.writeStringToFile(rootDirectoryConfigFile, sb.toString(), "UTF-8");
	}
	
	private String toPlatformIndependentPath(String path) {
		return path.replace(File.separatorChar, '/');
	}
}
