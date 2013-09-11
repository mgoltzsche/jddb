package de.algorythm.jdoe.model.dao.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

public class ArchiveManager {

	private File archive;
	private final File tmpDirectory;
	private final File tmpWorkingDirectory;
	private final File tmpArchiveDirectory;
	private final Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.BZIP2);
	private final File sourceFileNamePtr;
	
	public ArchiveManager(final File tmpDirectory) {
		this.tmpDirectory = tmpDirectory;
		tmpWorkingDirectory = new File(tmpDirectory.getAbsolutePath() + File.separator + "db");
		tmpArchiveDirectory = new File(tmpDirectory.getAbsolutePath() + File.separator + "archive");
		sourceFileNamePtr = new File(tmpDirectory.getAbsolutePath() + File.separator + "source-archive");
	}
	
	public boolean isRecoverable() {
		return sourceFileNamePtr.exists();
	}
	
	public File getTmpWorkingDirectory() {
		return tmpWorkingDirectory;
	}
	
	public void open(final File archive) throws IOException {
		if (this.archive != null)
			throw new IllegalStateException("archive already opened");
		
		if (archive != null) {
			if (!tmpWorkingDirectory.exists())
				tmpWorkingDirectory.mkdirs();
			
			if (archive.exists())
				archiver.extract(archive, tmpWorkingDirectory);
			else if (!archive.createNewFile())
				throw new IllegalArgumentException("Cannot create file at " + archive.getAbsolutePath());
			
			FileUtils.writeStringToFile(sourceFileNamePtr, archive.getAbsolutePath(), "UTF-8");
			
			this.archive = archive;
		} else if (sourceFileNamePtr.exists()) {
			this.archive = new File(FileUtils.readFileToString(sourceFileNamePtr, "UTF-8"));
		} else {
			throw new IllegalArgumentException("Nothing to recover");
		}
	}
	
	public void close() throws IOException {
		if (archive == null)
			throw new IllegalStateException("No archive opened");
		if (!tmpWorkingDirectory.exists())
			throw new IllegalStateException("Missing tmp db directory");
		if (!tmpArchiveDirectory.exists())
			tmpArchiveDirectory.mkdirs();
		
		final File tmpArchive = archiver.create("archive", tmpArchiveDirectory, tmpWorkingDirectory);
		
		tmpArchive.renameTo(archive);
		
		FileUtils.deleteDirectory(tmpDirectory);
		
		archive = null;
	}
}
