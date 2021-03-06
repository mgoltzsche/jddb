package de.algorythm.jddb.model.dao.impl.blueprints.propertyVisitor;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.entity.IPropertyValueVisitor;

public class IndexKeywordCollectingVisitor<REF extends IEntityReference> implements IPropertyValueVisitor<REF> {
	
	private final Set<String> indexKeywords;
	private final Pattern wordPattern;
	
	public IndexKeywordCollectingVisitor(final Pattern wordPattern, final Set<String> indexKeywords) {
		this.wordPattern = wordPattern;
		this.indexKeywords = indexKeywords;
	}
	
	protected boolean valueChanged(final IEntityReference oldValue, final IEntityReference newValue) {
		return oldValue == null && newValue != null || oldValue != null && !oldValue.equals(newValue);
	}
	
	@Override
	public void doWithAssociations(final IPropertyValue<Collection<REF>,REF> propertyValue) {}
	
	@Override
	public void doWithAssociation(final IPropertyValue<REF,REF> propertyValue) {}

	@Override
	public void doWithBoolean(final IPropertyValue<Boolean,?> propertyValue) {
		addIndexKeywords(propertyValue);
	}

	@Override
	public void doWithDecimal(final IPropertyValue<Long,?> propertyValue) {
		addIndexKeywords(propertyValue);
	}

	@Override
	public void doWithReal(final IPropertyValue<Double,?> propertyValue) {
		addIndexKeywords(propertyValue);
	}

	@Override
	public void doWithDate(final IPropertyValue<Date,?> propertyValue) {
		addIndexKeywords(propertyValue);
	}

	@Override
	public void doWithString(final IPropertyValue<String,?> propertyValue) {
		addIndexKeywords(propertyValue);
	}

	@Override
	public void doWithText(final IPropertyValue<String,?> propertyValue) {
		addIndexKeywords(propertyValue);
	}
	
	@Override
	public void doWithFile(final IPropertyValue<String,?> propertyValue) {
		if (propertyValue.getProperty().isSearchable()) {
			final String value = propertyValue.getValue();
			
			if (value != null && !value.isEmpty()) {
				final String fileName = value.replaceAll("(.*/)?(.*?)", "$2");
				
				indexKeywords.add(value);
				
				for (String keyword : truncatedKeywords(fileName))
					indexKeywords.add(keyword);
			}
		}
	}
	
	private void addIndexKeywords(IPropertyValue<?,?> propertyValue) {
		if (propertyValue.getProperty().isSearchable()) {
			final Object value = propertyValue.getValue();
			
			if (value != null) {
				final String valueStr = value.toString();
				
				if (!valueStr.isEmpty()) {
					for (String keyword : truncatedKeywords(valueStr))
						indexKeywords.add(keyword);
				}
			}
		}
	}
	
	private Iterable<String> truncatedKeywords(final String value) {
		final LinkedList<String> keywords = new LinkedList<>();
		
		if (value != null) {
			final String lowerCaseValue = value.toLowerCase();
			final Matcher matcher = wordPattern.matcher(lowerCaseValue);
			
			while (matcher.find()) {
				final String foundWord = matcher.group();
				
				for (int i = 1; i <= foundWord.length(); i++) {
					final String truncatedWord = foundWord.substring(0, i);
					
					keywords.add(truncatedWord);
				}
			}
		}
		
		return keywords;
	}
}
