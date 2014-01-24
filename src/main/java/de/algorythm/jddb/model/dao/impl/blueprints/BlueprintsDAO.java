package de.algorythm.jddb.model.dao.impl.blueprints;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import com.tinkerpop.blueprints.CloseableIterable;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;

import de.algorythm.jddb.model.dao.IDAO;
import de.algorythm.jddb.model.dao.IDAOTransactionContext;
import de.algorythm.jddb.model.dao.IModelFactory;
import de.algorythm.jddb.model.dao.IObserver;
import de.algorythm.jddb.model.dao.IPropertyValueLoader;
import de.algorythm.jddb.model.dao.ISchema;
import de.algorythm.jddb.model.dao.ModelChange;
import de.algorythm.jddb.model.dao.impl.blueprints.propertyVisitor.DeleteVisitor;
import de.algorythm.jddb.model.dao.impl.blueprints.propertyVisitor.IDAOVisitorContext;
import de.algorythm.jddb.model.dao.impl.blueprints.propertyVisitor.IndexKeywordCollectingVisitor;
import de.algorythm.jddb.model.dao.impl.blueprints.propertyVisitor.LoadVisitor;
import de.algorythm.jddb.model.dao.impl.blueprints.propertyVisitor.SaveVisitor;
import de.algorythm.jddb.model.entity.IEntity;
import de.algorythm.jddb.model.entity.IEntityReference;
import de.algorythm.jddb.model.entity.IPropertyValue;
import de.algorythm.jddb.model.meta.MEntityType;
import de.algorythm.jddb.model.meta.MEntityTypeWildcard;
import de.algorythm.jddb.model.meta.Schema;

public abstract class BlueprintsDAO<V extends IEntity<P, REF>, P extends IPropertyValue<?, REF>, REF extends IEntityReference>
		implements IDAO<V, P, REF>, IDAOVisitorContext<V, P, REF>,
		IDAOTransactionContext<V, P, REF> {

	static protected final String ID = "_id";
	static protected final String TYPE_FIELD = "_type";
	static protected final String SEARCH_INDEX = "searchIndex";
	static private final Pattern WORD_PATTERN = Pattern.compile("[\\w]+");
	static private final String DEFAULT_SCHEMA_RES = "/jdoe.schema.yaml";
	static private final String SCHEMA_FILE_NAME = "schema.yaml";

	private final IModelFactory<V, P, REF> modelFactory;
	private final Yaml yaml;
	private Schema schema;
	private File dbDirectory;
	private TransactionalGraph graph;
	private Index<Vertex> searchIndex;
	private final HashSet<IObserver<V, P, REF>> observers = new HashSet<>();
	private ModelChange<V, P, REF> change;
	private final File defaultSchemaFile;
	private File schemaFile;

	public BlueprintsDAO(final IModelFactory<V, P, REF> modelFactory) throws URISyntaxException {
		final URL schemaURL = getClass().getResource(DEFAULT_SCHEMA_RES);
		
		if (schemaURL == null)
			throw new RuntimeException("Missing default schema resource " + DEFAULT_SCHEMA_RES);
		
		final URI schemaURI = schemaURL.toURI();
		this.modelFactory = modelFactory;
		final Representer representer = new Representer();
		representer.getPropertyUtils().setSkipMissingProperties(true);
		yaml = new Yaml(representer);
		defaultSchemaFile = new File(schemaURI);
	}

	@Override
	public boolean isOpened() {
		return graph != null;
	}

	protected abstract TransactionalGraph openGraph(File dbDirectory);
	protected abstract Index<Vertex> getOrCreateSearchIndex();
	protected abstract void dropSearchIndex();

	@Override
	public void open(final File dbDirectory) throws IOException {
		this.dbDirectory = dbDirectory;
		schemaFile = new File(dbDirectory.getAbsolutePath()
				+ File.separator + SCHEMA_FILE_NAME);
		
		loadSchema(schemaFile.exists() ? schemaFile : defaultSchemaFile);
		graph = openGraph(dbDirectory);
		searchIndex = getOrCreateSearchIndex();
	}

	@Override
	public void close() throws IOException {
		graph.shutdown();
		dbDirectory = null;
	}

	private void loadSchema(final File schemaFile) throws IOException {
		schema = schemaFile.exists()
			? yaml.loadAs(new FileReader(schemaFile), Schema.class)
			: new Schema();
	}

	@Override
	public ISchema getSchema() {
		if (!isOpened())
			throw new IllegalStateException("No database opened");
		
		return schema;
	}

	@Override
	public void updateSchemaTypes(final Collection<MEntityType> types) throws IOException {
		if (!isOpened())
			throw new IllegalStateException("No database opened");
		
		final Schema newSchema = new Schema();
		newSchema.setTypes(types);
		yaml.dump(schema, new FileWriter(schemaFile));
		schema = newSchema;
	}

	@Override
	public V createNewEntity(final MEntityType type) {
		return modelFactory.createNewEntity(type);
	}

	@Override
	public REF createEntityReference(final Vertex vertex) {
		return modelFactory.createEntityReference(createLoader(vertex));
	}

	private V createEntity(final Vertex vertex) {
		return modelFactory.createEntity(createLoader(vertex));
	}

	private IPropertyValueLoader<REF> createLoader(final Vertex vertex) {
		final String id = vertex.<String> getProperty(ID);
		final String typeName = vertex.<String> getProperty(TYPE_FIELD);

		if (id == null || typeName == null)
			throw new IllegalStateException(String.format(
					"id or type of vertex %s is null. Available keys: %s",
					vertex, vertex.getPropertyKeys()));

		final MEntityType type = schema.getTypeByName(typeName);

		return new LoadVisitor<>(vertex, id, type, this);
	}

	@Override
	public Collection<REF> loadReferringEntities(final String entityId,
			final Vertex vertex) {
		final LinkedList<REF> entities = new LinkedList<>();

		for (Edge edge : vertex.getEdges(Direction.IN)) {
			final Vertex referringVertex = edge.getVertex(Direction.OUT);
			final REF referringEntity = createEntityReference(referringVertex);

			if (!entityId.equals(referringEntity.getId()))
				entities.add(referringEntity);
		}

		return entities;
	}

	@Override
	public void save(final V entity) {
		save(entity, new HashMap<V, Vertex>());
	}

	@Override
	public Vertex save(final V entity, final Map<V, Vertex> savedEntities) {
		change.getSaved().put(entity.getId(), entity);

		Vertex vertex = savedEntities.get(entity);

		if (vertex != null)
			return vertex;

		Set<String> oldIndexKeywords;

		try {
			vertex = findVertex(entity);
			oldIndexKeywords = createIndexKeywords(createEntity(vertex));
		} catch (IllegalArgumentException e) {
			change.setNewOrDeleted(true);
			vertex = graph.addVertex(null);
			vertex.setProperty(ID, entity.getId());
			vertex.setProperty(TYPE_FIELD, entity.getType().getName());
			oldIndexKeywords = new HashSet<>();
		}

		savedEntities.put(entity, vertex);

		final Set<String> indexKeywords = new HashSet<>();
		final SaveVisitor<V, P, REF> visitor = new SaveVisitor<>(this, vertex,
				savedEntities, WORD_PATTERN, indexKeywords);

		// assign values to vertex
		for (IPropertyValue<?, REF> propertyValue : entity.getValues())
			propertyValue.visit(visitor);

		// update vertex index if keyword changed
		final HashSet<String> newIndexKeywords = new HashSet<>(indexKeywords);

		newIndexKeywords.removeAll(oldIndexKeywords);
		oldIndexKeywords.removeAll(indexKeywords);

		for (String keyword : oldIndexKeywords)
			// remove expired keywords
			searchIndex.remove(SEARCH_INDEX, keyword, vertex);

		for (String keyword : newIndexKeywords)
			// save new keywords
			searchIndex.put(SEARCH_INDEX, keyword, vertex);

		return vertex;
	}

	private Set<String> createIndexKeywords(final V entity) {
		final Set<String> indexKeywords = new HashSet<>();
		final IndexKeywordCollectingVisitor<REF> visitor = new IndexKeywordCollectingVisitor<>(
				WORD_PATTERN, indexKeywords);

		for (IPropertyValue<?, REF> propertyValue : entity.getValues())
			propertyValue.visit(visitor);

		return indexKeywords;
	}

	@Override
	public void delete(final REF entityRef) {
		delete(entityRef, new HashSet<REF>());
	}
	
	@Override
	public void delete(final REF entityRef, final Set<REF> deletedEntities) {
		if (!deletedEntities.add(entityRef))
			return;
		
		final Vertex vertex;

		try {
			vertex = findVertex(entityRef);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Cannot remove entity "
					+ entityRef + "(" + entityRef.getId()
					+ ") because it doesn't exist");
		}

		final V entity = createEntity(vertex);
		final Set<String> indexKeywords = new HashSet<>();
		final DeleteVisitor<V, P, REF> visitor = new DeleteVisitor<>(this,
				WORD_PATTERN, indexKeywords, deletedEntities);

		change.setNewOrDeleted(true);
		change.getDeleted().add(entity);

		// delete containments and collect index
		for (IPropertyValue<?, REF> value : entity.getValues())
			value.visit(visitor);

		// delete vertex index
		for (String keyword : indexKeywords)
			searchIndex.remove(SEARCH_INDEX, keyword, vertex);

		// delete all edges
		for (Edge edge : vertex.getEdges(Direction.BOTH))
			edge.remove();

		// delete vertex
		vertex.remove();
	}

	@Override
	public Set<V> list(final MEntityType type) {
		final LinkedHashSet<V> result = new LinkedHashSet<>();
		final Iterable<Vertex> vertices = type == MEntityTypeWildcard.INSTANCE ? graph
				.getVertices() : graph.getVertices(TYPE_FIELD, type.getName());

		for (Vertex vertex : vertices)
			result.add(createEntity(vertex));

		return result;
	}

	@Override
	public Set<V> list(final MEntityType type, final String search) {
		if (search == null || search.isEmpty())
			return list(type);

		final LinkedHashSet<V> result = new LinkedHashSet<>();
		final Iterable<String> searchKeywords = createSearchKeywords(search);

		LinkedHashSet<Vertex> foundVertices = null;

		for (String keyword : searchKeywords) {
			final LinkedHashSet<Vertex> keywordVertices = new LinkedHashSet<>();

			final CloseableIterable<Vertex> hits = searchIndex.get(
					SEARCH_INDEX, keyword);

			try {
				for (Vertex vertex : hits)
					keywordVertices.add(vertex);
			} finally {
				hits.close();
			}

			if (foundVertices == null)
				foundVertices = keywordVertices;
			else
				foundVertices.retainAll(keywordVertices);
		}

		if (foundVertices != null) {
			for (Vertex vertex : foundVertices) {
				final MEntityType vertexType = schema.getTypeByName(vertex
						.<String> getProperty(TYPE_FIELD));

				if (type.isConform(vertexType))
					result.add(createEntity(vertex));

				if (vertexType.isEmbedded()) {
					for (Vertex referringVertex : vertex
							.getVertices(Direction.IN)) {
						final MEntityType referringVertexType = schema
								.getTypeByName(referringVertex
										.<String> getProperty(TYPE_FIELD));

						if (type.isConform(referringVertexType)
								&& !foundVertices.contains(referringVertex))
							result.add(createEntity(referringVertex));
					}
				}
			}
		}

		return result;
	}

	private Iterable<String> createSearchKeywords(final String searchPhrase) {
		final LinkedList<String> result = new LinkedList<>();
		final String searchPhraseLowerCase = searchPhrase.toLowerCase();
		final Matcher matcher = WORD_PATTERN.matcher(searchPhraseLowerCase);

		while (matcher.find())
			result.add(matcher.group());

		return result;
	}

	@Override
	public void addObserver(final IObserver<V, P, REF> observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(final IObserver<V, P, REF> observer) {
		observers.remove(observer);
	}

	@Override
	public V find(final String id) {
		return createEntity(findVertex(id));
	}
	
	@Override
	public V find(final IEntityReference entityRef) {
		return createEntity(findVertex(entityRef));
	}

	@Override
	public Vertex findVertex(final IEntityReference entityRef) {
		return findVertex(entityRef.getId());
	}
	
	private Vertex findVertex(final String id) {
		final Iterator<Vertex> iter = graph.getVertices(ID, id)
				.iterator();

		if (!iter.hasNext())
			throw new IllegalArgumentException("vertex with id "
					+ id + " does not exist");

		return iter.next();
	}

	@Override
	public boolean exists(final IEntityReference entityRef) {
		try {
			findVertex(entityRef);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public void transaction(
			Procedure1<IDAOTransactionContext<V, P, REF>> transaction) {
		change = new ModelChange<>();

		try {
			transaction.apply(this);
			graph.commit();
		} catch (Throwable e) {
			graph.rollback();
			throw e;
		}

		notifyObservers();
		change = null;
	}
	
	@Override
	public void rebuildIndex() {
		transaction(new Procedure1<IDAOTransactionContext<V,P,REF>>() {
			@Override
			public void apply(IDAOTransactionContext<V, P, REF> arg0) {
				dropSearchIndex();
				searchIndex = getOrCreateSearchIndex();
				
				for (Vertex vertex : graph.getVertices()) {
					final String typeName = vertex.<String>getProperty(TYPE_FIELD);
					
					if (schema.isKnownType(typeName)) {
						final V entity = createEntity(vertex);
						
						for (String keyword : createIndexKeywords(entity))
							searchIndex.put(SEARCH_INDEX, keyword, vertex);
					}
				}
			}
		});
	}

	private void notifyObservers() {
		for (IObserver<V, P, REF> observer : new LinkedList<>(observers))
			observer.update(change);
	}
}