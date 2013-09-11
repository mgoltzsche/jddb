package de.algorythm.jdoe;

import java.io.File;

public class Config {

	private final File preferencesDirectory;
	private final File tmpDirectory;
	
	public Config(final File preferencesDirectory, final File tmpDirectory) {
		this.preferencesDirectory = preferencesDirectory;
		this.tmpDirectory = tmpDirectory;
	}

	public File getPreferencesDirectory() {
		return preferencesDirectory;
	}

	public File getTmpDirectory() {
		return tmpDirectory;
	}
}