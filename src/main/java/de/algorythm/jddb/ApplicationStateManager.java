package de.algorythm.jddb;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationStateManager {

	static private final Logger log = LoggerFactory.getLogger(ApplicationStateManager.class);
	
	private final File stateFile;
	private File openDatabaseFile;
	private List<String> openEntityIds;
	
	public ApplicationStateManager(final File stateFile) {
		this.stateFile = stateFile;
	}
	
	public File getOpenDatabaseFile() {
		return openDatabaseFile;
	}
	
	public Iterable<String> getOpenEntityIds() {
		return openEntityIds;
	}
	
	@SuppressWarnings("unchecked")
	public void loadApplicationState() {
		final List<String> lines;
		
		try {
			lines = (List<String>) FileUtils.readLines(stateFile, "UTF-8");
		} catch (IOException e) {
			log.debug("Cannot read " + stateFile);
			return;
		}
		
		openEntityIds = new LinkedList<>();
		
		for (int i = 0; i < lines.size(); i++) {
			if (i == 0) {
				final File dbFile = new File(lines.get(i));
				
				if (!dbFile.exists())
					return;
				
				openDatabaseFile = dbFile;
			} else {
				openEntityIds.add(lines.get(i));
			}
		}
	}
	
	public void saveApplicationState(final File openDatabaseFile, final List<String> openEntityIds) throws IOException {
		this.openDatabaseFile = openDatabaseFile;
		this.openEntityIds = openEntityIds;
		
		if (openDatabaseFile != null) {
			final StringBuilder sb = new StringBuilder();
			
			sb.append(openDatabaseFile.getAbsolutePath());
			
			for (String entityId : openEntityIds)
				sb.append("\n").append(entityId);
			
			FileUtils.writeStringToFile(stateFile, sb.toString());
			log.debug("application state saved");
		}
	}
}
