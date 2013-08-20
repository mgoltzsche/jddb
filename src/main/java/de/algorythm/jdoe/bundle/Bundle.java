package de.algorythm.jdoe.bundle;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Bundle {
	
	static private final String BUNDLE_NAME = "bundles/jdoe";
	static private final Bundle instance = new Bundle();
	
	static public Bundle getInstance() {
		return instance;
	}
	
	public final ResourceBundle bundle;
	public final String typeDefinition;
	public final String types;
	public final String type;
	public final String properties;
	public final String searchable;
	public final String contained;
	public final String add;
	public final String save;
	public final String delete;
	public final String remove;
	public final String create;
	public final String edit;
	public final String yes;
	public final String no;
	
	public final String open;
	public final String configure;
	public final String search;
	public final String all;
	public final String newEntity;
	public final String results;
	public final String entity;
	
	private Bundle() {
		ResourceBundle b;
		
		try {
			b = ResourceBundle.getBundle(BUNDLE_NAME);
		} catch(MissingResourceException e) {
			b = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
		}
		
		bundle = b;
		
		typeDefinition = b.getString("typeDefinition");
		types = b.getString("types");
		type = b.getString("type");
		properties = b.getString("properties");
		searchable = b.getString("searchable");
		contained = b.getString("contained");
		add = b.getString("add");
		save = b.getString("save");
		delete = b.getString("delete");
		remove = b.getString("remove");
		create = b.getString("create");
		edit = b.getString("edit");
		open = b.getString("open");
		configure = b.getString("configure");
		search = b.getString("search");
		all = b.getString("all");
		newEntity = b.getString("newEntity");
		results = b.getString("results");
		entity = b.getString("entity");
		yes = b.getString("yes");
		no = b.getString("no");
	}
}
