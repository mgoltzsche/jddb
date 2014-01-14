package de.algorythm.jdoe.ui.jfx.util;

import java.util.LinkedList;

import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.ui.jfx.model.FXEntityReference;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.BooleanFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.DateFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.DecimalFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociation;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FXAssociations;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.FileFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.IFXPropertyValueVisitor;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.RealFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.StringFXAttributeValue;
import de.algorythm.jdoe.ui.jfx.model.propertyValue.TextFXAttributeValue;

public class FilePropertyUtil {

	static private class FilePathCollector implements IFXPropertyValueVisitor {

		private final LinkedList<String> filePathes;

		public FilePathCollector(final LinkedList<String> filePathes) {
			this.filePathes = filePathes;
		}

		@Override
		public void doWithBoolean(BooleanFXAttributeValue propertyValue) {
		}

		@Override
		public void doWithDecimal(DecimalFXAttributeValue propertyValue) {
		}

		@Override
		public void doWithReal(RealFXAttributeValue propertyValue) {
		}

		@Override
		public void doWithDate(DateFXAttributeValue propertyValue) {
		}

		@Override
		public void doWithString(StringFXAttributeValue propertyValue) {
		}

		@Override
		public void doWithText(TextFXAttributeValue propertyValue) {
		}

		@Override
		public void doWithAssociation(FXAssociation propertyValue) {
		}

		@Override
		public void doWithAssociations(FXAssociations propertyValue) {
		}

		@Override
		public void doWithFile(FileFXAttributeValue propertyValue) {
			final String value = propertyValue.getValue();

			if (value != null)
				filePathes.add(value);
		}
	}

	public <REF extends IEntityReference> Iterable<String> getFilePathes(
			final FXEntityReference entityRef) {
		final LinkedList<String> filePathes = new LinkedList<>();
		
		entityRef.visit(new FilePathCollector(filePathes));

		return filePathes;
	}
}
