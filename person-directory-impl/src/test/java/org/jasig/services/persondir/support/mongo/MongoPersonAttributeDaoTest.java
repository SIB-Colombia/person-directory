package org.jasig.services.persondir.support.mongo;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.AbstractDefaultAttributePersonAttributeDao;
import org.jasig.services.persondir.support.AbstractQueryPersonAttributeDao;
import org.jasig.services.persondir.support.SimpleUsernameAttributeProvider;
import org.jasig.services.persondir.support.mongo.MongoPersonAttributeDao;
import org.jasig.services.persondir.util.Util;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static java.util.Arrays.asList;

/**
 * Test the {@link MongoPersonAttributeDao} against a dummy DataSource.
 * 
 * @author Valentina Grajales <valegrajales@gmail.com>
 * @version $Revision$ $Date$
 */
public class MongoPersonAttributeDaoTest extends AbstractMongoDBTest {

	@Override
	protected void setUpSchema(final MongoClient dataSource) {
		MongoDatabase db = dataSource.getDatabase("cas");
		MongoCollection<Document> collection = db.getCollection("users");
		Document doc = new Document("email", "test@test.dummy")
				.append("password", "invalidpassword")
				.append("firstname", "Jon")
				.append("lastname", "Doe")
				.append("roles", asList("user", "admin"));
		collection.insertOne(doc);
		doc = new Document("email", "test2@test2.dummy")
				.append("password", "invalidpassword2")
				.append("roles", asList("user"));
		collection.insertOne(doc);
	}

	@Override
	protected void tearDownSchema(final MongoClient dataSource) {
		MongoDatabase db = dataSource.getDatabase("cas");
		db.drop();
		dataSource.close();
	}
	
	@Override
	protected AbstractQueryPersonAttributeDao<Document> newDao(MongoClient dataSource) {
		return new MongoPersonAttributeDao(dataSource, "cas", "users");
	}
	
	public void testNoQueryAttributeMapping() {
		final MongoPersonAttributeDao impl = new MongoPersonAttributeDao(testDataSource, "cas", "users");
		impl.setUsernameAttributeProvider(new SimpleUsernameAttributeProvider("email"));
		
		final Map<String, Object> columnsToAttributes = new HashMap<>();
		columnsToAttributes.put("firstname", "firstName");
		columnsToAttributes.put("lastname", "lastname");
		columnsToAttributes.put("roles", "roles");
		columnsToAttributes.put("email", "email");
		impl.setResultAttributeMapping(columnsToAttributes);
		
		final Map<String, List<Object>> attribs = impl.getMultivaluedUserAttributes("test@test.dummy");
		assertEquals(Util.list("test@test.dummy"), attribs.get("email"));
		assertEquals(Util.list("Jon"), attribs.get("firstName"));
		assertEquals(Util.list("Doe"), attribs.get("lastname"));
	}

	@Override
	protected AbstractDefaultAttributePersonAttributeDao getAbstractDefaultQueryPersonAttributeDao() {
		final String queryAttr = "shirt";
		final List<String> queryAttrList = new LinkedList<>();
		queryAttrList.add(queryAttr);

		// shirt_color = ?

		final MongoPersonAttributeDao impl = new MongoPersonAttributeDao();
		impl.setQueryAttributeMapping(Collections.singletonMap("shirt", "shirt_color"));

		return impl;
	}
}