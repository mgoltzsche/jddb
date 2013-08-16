package de.algorythm.jdoe.model.dao.impl.orientdb.visitor;

import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.algorythm.jdoe.model.entity.IPropertyValue;
import de.algorythm.jdoe.model.entity.IPropertyValueVisitor;
import de.algorythm.jdoe.model.entity.impl.Association;
import de.algorythm.jdoe.model.entity.impl.Associations;
import de.algorythm.jdoe.model.entity.impl.BooleanValue;
import de.algorythm.jdoe.model.entity.impl.DateValue;
import de.algorythm.jdoe.model.entity.impl.DecimalValue;
import de.algorythm.jdoe.model.entity.impl.RealValue;
import de.algorythm.jdoe.model.entity.impl.StringValue;
import de.algorythm.jdoe.model.entity.impl.TextValue;

public class IndexKeywordCollectingVisitor implements IPropertyValueVisitor {
	
	private final Set<String> indexKeywords;
	private final Pattern wordPattern;
	
	public IndexKeywordCollectingVisitor(final Pattern wordPattern, final Set<String> indexKeywords) {
		this.wordPattern = wordPattern;
		this.indexKeywords = indexKeywords;
	}
	
	@Override
	public void doWithAssociations(Associations propertyValue) {}
	
	@Override
	public void doWithAssociation(Association propertyValue) {}

	@Override
	public void doWithBoolean(BooleanValue propertyValue) {
		addIndexKeywords(propertyValue);
	}

	@Override
	public void doWithDecimal(DecimalValue propertyValue) {
		addIndexKeywords(propertyValue);
	}

	@Override
	public void doWithReal(RealValue propertyValue) {
		addIndexKeywords(propertyValue);
	}

	@Override
	public void doWithDate(DateValue propertyValue) {
		addIndexKeywords(propertyValue);
	}

	@Override
	public void doWithString(StringValue propertyValue) {
		addIndexKeywords(propertyValue);
	}

	@Override
	public void doWithText(TextValue propertyValue) {
		addIndexKeywords(propertyValue);
	}
	
	private void addIndexKeywords(IPropertyValue<?> propertyValue) {
		if (propertyValue.getProperty().isSearchable()) {
			final String value = propertyValue.toString();
			
			if (!value.isEmpty())
				for (String keyword : truncatedKeywords(value))
					indexKeywords.add(keyword);
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
