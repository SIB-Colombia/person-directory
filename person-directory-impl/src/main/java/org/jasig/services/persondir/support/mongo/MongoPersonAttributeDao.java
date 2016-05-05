package org.jasig.services.persondir.support.mongo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.bson.Document;
import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.AbstractQueryPersonAttributeDao;
import org.jasig.services.persondir.support.CaseInsensitiveAttributeNamedPersonImpl;
import org.jasig.services.persondir.support.CaseInsensitiveNamedPersonImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoPersonAttributeDao extends AbstractQueryPersonAttributeDao<Document> {

	/** Logger instance. **/
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	/* MongoDB Database connector */
	private MongoDatabase database;

	/* MongoDB Collection set */
	MongoCollection<Document> collection;

	/* URL of MongoDB Host */
	private String mongoHostUrl;

	/* MongoDB port */
	private String mongoPort;

	/* MongoDB database */
	private String mongoDB;

	/* MongoDB attributes collection */
	private String mongoCollection;

	public MongoPersonAttributeDao() {
		super();
		/*this.connectionString = new MongoClientURI("mongodb://"+this.mongoHostUrl+":"+this.mongoPort);
		this.mongoClient = new MongoClient(connectionString);
		this.database = mongoClient.getDatabase(this.mongoDB);
		this.collection = database.getCollection(this.mongoCollection);*/
	}

	public MongoPersonAttributeDao(MongoClient dataSource, String db, String collection) {
		super();
		this.database = dataSource.getDatabase(db);
		this.collection = database.getCollection(collection);
	}

	/**
	 * Initializes the object after properties are set.
	 */
	/*@PostConstruct
	public void initialize() {
		this.connectionString = new MongoClientURI("mongodb://"+this.mongoHostUrl+":"+this.mongoPort);
		this.mongoClient = new MongoClient(connectionString);
		this.database = mongoClient.getDatabase(this.mongoDB);
		this.collection = database.getCollection(this.mongoCollection);
	}*/

	@Override
	protected List<IPersonAttributes> getPeopleForQuery(final Document filter, final String userName) {
		try{
			Document user = this.collection.find(filter).first();

			final List<IPersonAttributes> personAttributes = new ArrayList<>();
			Map<String, List<Object>> personAttribute = new HashMap<String, List<Object>>();
			for(Map.Entry<String, Object> entry : user.entrySet()) {

				if (entry.getValue() instanceof List) {
					personAttribute.put(entry.getKey(), (List) entry.getValue()); 
				} else {
					//System.out.println("no lista");
					personAttribute.put(entry.getKey(), Arrays.asList(entry.getValue()));
				}
			}
			final IPersonAttributes person;
			person = new CaseInsensitiveNamedPersonImpl(userName, personAttribute);
			personAttributes.add(person);
			return personAttributes;
		} catch(MongoException e) {
			logger.info("MongoDB can't find attributes in collection: ", e);
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return null;
		}
	}

	@Override
	protected Document appendAttributeToQuery(final Document filter, final String dataAttribute,
			List<Object> queryValues) {
		final Document query = new Document();
		if (filter == null && queryValues.size() > 0) {
			for (final Object queryValue : queryValues) {
				query.append(dataAttribute, queryValue);
			}
			logger.debug("Constructed MongoDB search query [{}]", query.toString());
		} else {
			throw new UnsupportedOperationException("Multiple attributes not supported.");
		}
		return query;
	}

	public String getMongoHostUrl() {
		return mongoHostUrl;
	}

	public void setMongoHostUrl(String mongoHostUrl) {
		this.mongoHostUrl = mongoHostUrl;
	}

	public String getMongoPort() {
		return mongoPort;
	}

	public void setMongoPort(String mongoPort) {
		this.mongoPort = mongoPort;
	}

	public String getMongoDB() {
		return mongoDB;
	}

	public void setMongoDB(String mongoDB) {
		this.mongoDB = mongoDB;
	}

	public String getMongoCollection() {
		return mongoCollection;
	}

	public void setMongoCollection(String mongoCollection) {
		this.mongoCollection = mongoCollection;
	}

}
