package org.jasig.services.persondir.support.mongo;

import org.jasig.services.persondir.support.AbstractDefaultQueryPersonAttributeDaoTest;
import org.jasig.services.persondir.support.AbstractQueryPersonAttributeDao;

import com.mongodb.MongoClient;
import org.bson.Document;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;

public abstract class AbstractMongoDBTest extends AbstractDefaultQueryPersonAttributeDaoTest {
	
	protected static final MongodStarter starter = MongodStarter.getDefaultInstance();
	
	protected abstract void setUpSchema(MongoClient dataSource);
	protected abstract void tearDownSchema(MongoClient dataSource);
	protected abstract AbstractQueryPersonAttributeDao<Document> newDao(MongoClient dataSource);
	
	protected MongodExecutable _mongodExe;
	protected MongodProcess _mongod;

	protected MongoClient testDataSource;
	
	@Override
	public void setUp() throws Exception {
		_mongodExe = starter.prepare(new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.net(new Net(12345, true))
				.build());
		_mongod = _mongodExe.start();

		super.setUp();

		this.testDataSource = new MongoClient("localhost", 12345);
		
		setUpSchema(testDataSource);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		tearDownSchema(testDataSource);
		this.testDataSource.close();
		_mongod.stop();
		_mongodExe.stop();
	}
}
