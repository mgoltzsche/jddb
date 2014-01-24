package de.algorythm.jddb.bundle;

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
	public final String close;
	public final String unknown;
	public final String typeDefinition;
	public final String types;
	public final String type;
	public final String properties;
	public final String embedded;
	public final String searchable;
	public final String contained;
	public final String dataSet;
	public final String add;
	public final String save;
	public final String delete;
	public final String remove;
	public final String create;
	public final String edit;
	public final String choose;
	public final String yes;
	public final String no;
	
	public final String open;
	public final String configure;
	public final String search;
	public final String all;
	public final String newEntity;
	public final String results;
	public final String entity;
	public final String updateDatabaseSchema;
	public final String rebuildSearchIndex;
	
	public final String stateReady;
	public final String stateQueued;
	public final String stateRunning;
	public final String stateCanceled;
	public final String stateCompleted;
	public final String stateFailed;
	public final String taskOpenDB;
	public final String taskCloseDB;
	public final String taskSearch;
	public final String taskSwitchSearchType;
	public final String taskLoad;
	public final String taskSave;
	public final String taskDelete;
	public final String taskCloseTransientContainmentEditors;
	public final String taskDetails;
	public final String tasksQueued;
	public final String tasksFailed;
	
	private Bundle() {
		ResourceBundle b;
		
		try {
			b = ResourceBundle.getBundle(BUNDLE_NAME);
		} catch(MissingResourceException e) {
			b = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
		}
		
		bundle = b;
		
		close = b.getString("close");
		unknown = b.getString("unknown");
		typeDefinition = b.getString("typeDefinition");
		types = b.getString("types");
		type = b.getString("type");
		properties = b.getString("properties");
		embedded = b.getString("embedded");
		searchable = b.getString("searchable");
		contained = b.getString("contained");
		dataSet = b.getString("dataSet");
		add = b.getString("add");
		save = b.getString("save");
		delete = b.getString("delete");
		remove = b.getString("remove");
		create = b.getString("create");
		edit = b.getString("edit");
		choose = b.getString("choose");
		open = b.getString("open");
		configure = b.getString("configure");
		search = b.getString("search");
		all = b.getString("all");
		newEntity = b.getString("newEntity");
		results = b.getString("results");
		entity = b.getString("entity");
		updateDatabaseSchema = b.getString("updateDatabaseSchema");
		rebuildSearchIndex = b.getString("rebuildSearchIndex");
		yes = b.getString("yes");
		no = b.getString("no");
		stateReady = b.getString("stateReady");
		stateQueued = b.getString("stateQueued");
		stateRunning = b.getString("stateRunning");
		stateCanceled = b.getString("stateCanceled");
		stateCompleted = b.getString("stateCompleted");
		stateFailed = b.getString("stateFailed");
		taskOpenDB = b.getString("taskOpenDB");
		taskCloseDB = b.getString("taskCloseDB");
		taskSearch = b.getString("taskSearch");
		taskSwitchSearchType = b.getString("taskSwitchSearchType");
		taskLoad = b.getString("taskLoad");
		taskSave = b.getString("taskSave");
		taskDelete = b.getString("taskDelete");
		taskCloseTransientContainmentEditors = b.getString("taskCloseTransientContainmentEditors");
		tasksQueued = b.getString("tasksQueued");
		tasksFailed = b.getString("tasksFailed");
		taskDetails = b.getString("taskDetails");
	}
}
