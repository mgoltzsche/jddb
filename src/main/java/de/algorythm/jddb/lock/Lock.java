package de.algorythm.jddb.lock;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.algorythm.jddb.Config;

@Singleton
@Deprecated
public class Lock {

	@Inject
	private Config cfg;
	private FileLock fileLock;
	RandomAccessFile lockFile;
	
	public void lock() throws IOException {
		final File lockDir = cfg.getPreferencesDirectory();
		final File lock = new File(lockDir.getAbsolutePath() + File.separator + "lock");
		
		if (!lockDir.exists())
			lockDir.mkdirs();
		
		if (!lock.exists())
			lock.createNewFile();
		
		lockFile = new RandomAccessFile(lock, "rw");
		
		try {
			fileLock = lockFile.getChannel().tryLock();
			
			if (fileLock == null || !fileLock.isValid())
				throw new IllegalStateException("tmp directory is already locked by another process");
		} finally {
			lockFile.close();
		}
	}
	
	public void unlock() throws IOException {
		if (fileLock != null) {
			fileLock.release();
			lockFile.close();
		}
	}
}
