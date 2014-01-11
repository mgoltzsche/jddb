package de.algorythm.jdoe.ui.jfx.util;

import java.awt.Desktop;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenFileUtil {
	
	static private final Logger LOG = LoggerFactory.getLogger(OpenFileUtil.class);
	static private interface IFileOpenAction {
		boolean isOpenFileSupported();
		void openFileExternally(String filePath);
	}
	static private final IFileOpenAction impl;
	
	static private class UnsupportedFileOpenAction implements IFileOpenAction {
		@Override
		public boolean isOpenFileSupported() {
			return false;
		}
		@Override
		public void openFileExternally(final String filePath) {
		}
	}
	
	static private class FileOpenAction implements IFileOpenAction {
		@Override
		public boolean isOpenFileSupported() {
			return true;
		}
		@Override
		public void openFileExternally(final String filePath) {
			if (filePath == null || filePath.isEmpty())
				return;
			
			final File file = new File(filePath);
			
			if (!file.exists())
				return;
			
			try {
				Desktop.getDesktop().open(file);
			} catch(Throwable e) {
				LOG.error("Cannot open file externally: " + file.getAbsolutePath());
			}
		}
	}
	
	static {
		if (Desktop.isDesktopSupported()) {
			impl = new FileOpenAction();
		} else {
			impl = new UnsupportedFileOpenAction();
			LOG.warn("AWT Desktop is not supported");
		}
	}
	
	static public boolean isOpenFileSupported() {
		return impl.isOpenFileSupported();
	}
	
	static public void openFileExternally(final String filePath) {
		impl.openFileExternally(filePath);
	}
}
