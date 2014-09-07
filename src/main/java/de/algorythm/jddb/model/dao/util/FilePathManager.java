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
		int maxRootPathLength = 0;
		
		for (File rootDir : rootDirectories) {
			final String absoluteRootPath = rootDir.getAbsolutePath();
			final String absoluteRootPathSlash = absoluteRootPath + File.separator;
			final int rootPathLength = absoluteRootPathSlash.length();
			
			if ((absolutePath.startsWith(absoluteRootPathSlash) ||
					absolutePath.equals(absoluteRootPath)) &&
					rootPathLength > maxRootPathLength)
				maxRootPathLength = rootPathLength;
		}
		
		if (maxRootPathLength == 0)
			throw new IllegalArgumentException("Cannot refer file that is not contained in a JDDB root directory (see settings): " + absolutePath);
		
		return toPlatformIndependentPath(maxRootPathLength >= absolutePath.length() ? "." : absolutePath.substring(maxRootPathLength));
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
	
	@Override
	public File getPrimaryRootDirectory() {
		return rootDirectories.getFirst();
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
