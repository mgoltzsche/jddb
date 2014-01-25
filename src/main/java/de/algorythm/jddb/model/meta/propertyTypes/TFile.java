package de.algorythm.jddb.model.meta.propertyTypes;

import java.io.File;

import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.entity.IPropertyValueFactory;
import de.algorythm.jddb.model.meta.MProperty;

public class TFile extends TString {

	static private final long serialVersionUID = 6086451869372645461L;
	static private TFile instance;
	
	static public AbstractAttributeType<?> getInstance() {
		if (instance == null)
			instance = new TFile();
		
		return instance;
	}
	
	private TFile() {
		super("file", "File");
	}
	
	@Override
	public <P extends IPropertyValue<?,? extends IEntityReference>> P createPropertyValue(final MProperty property, final IPropertyValueFactory<P> factory) {
		return factory.createFileAttributeValue(property, this);
	}
	
	@Override
	public void valueToString(final String value, final StringBuilder sb) {
		if (value != null)
			sb.append(new File(value).getName());
	}
}
