package de.algorythm.jddb.ui.jfx.util;

import java.awt.Desktop;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.algorythm.jddb.model.dao.util.IFilePathConverter;

public class OpenFileUtil {
	
	static private final Logger LOG = LoggerFactory.getLogger(OpenFileUtil.class);
	static private interface IFileOpenAction {
		boolean isOpenFileSupported();
		void openFileExternally(String filePath);
	}
	static private final IFileOpenAction impl;
	static private IFilePathConverter pathConverter;
	
	static public void setFilePathConverter(final IFilePathConverter converter) {
		pathConverter = converter;
	}
	
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
	
	static public void openFileExternally(String filePath) {
		filePath = pathConverter.toAbsolutePath(filePath);
		
		impl.openFileExternally(filePath);
	}
}
