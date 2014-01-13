package de.algorythm.jdoe.ui.jfx.util;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import de.algorythm.jdoe.model.entity.IEntity;
import de.algorythm.jdoe.model.entity.IEntityReference;
import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;

public class FilePropertyUtil {

	static private class FilePathCollector<REF extends IEntityReference>
			implements IPropertyValueVisitor<REF> {

		private final LinkedList<String> filePathes;

		public FilePathCollector(final LinkedList<String> filePathes) {
			this.filePathes = filePathes;
		}

		@Override
		public void doWithBoolean(IPropertyValue<Boolean, ?> propertyValue) {
		}

		@Override
		public void doWithDecimal(IPropertyValue<Long, ?> propertyValue) {
		}

		@Override
		public void doWithReal(IPropertyValue<Double, ?> propertyValue) {
		}

		@Override
		public void doWithDate(IPropertyValue<Date, ?> propertyValue) {
		}

		@Override
		public void doWithString(IPropertyValue<String, ?> propertyValue) {
		}

		@Override
		public void doWithText(IPropertyValue<String, ?> propertyValue) {
		}

		@Override
		public void doWithAssociation(IPropertyValue<REF, REF> propertyValue) {
		}

		@Override
		public void doWithAssociations(
				IPropertyValue<Collection<REF>, REF> propertyValue) {
		}

		@Override
		public void doWithFile(IPropertyValue<String, ?> propertyValue) {
			final String value = propertyValue.getValue();

			if (value != null)
				filePathes.add(value);
		}
	}

	public <REF extends IEntityReference> Iterable<String> getFilePathes(
			final IEntity<? extends IPropertyValue<?, REF>, REF> entity) {
		final LinkedList<String> filePathes = new LinkedList<>();
		final IPropertyValueVisitor<REF> visitor = new FilePathCollector<REF>(filePathes);

		for (IPropertyValue<?, REF> propertyValue : entity.getValues())
			propertyValue.visit(visitor);

		return filePathes;
	}
}
