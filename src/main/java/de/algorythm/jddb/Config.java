package de.algorythm.jddb;

import java.io.File;

public class Config {

	private final File preferencesDirectory;
	
	public Config(final File preferencesDirectory) {
		this.preferencesDirectory = preferencesDirectory;
	}

	public File getPreferencesDirectory() {
		return preferencesDirectory;
	}
}