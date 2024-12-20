package frontend.db;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.jackson.dataformat.velocypack.VelocyJack;
import com.arangodb.velocypack.module.jdk8.VPackJdk8Module;
import com.arangodb.velocypack.module.joda.VPackJodaModule;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DBUtil {

	private static final Logger logger = Logger.getLogger( "hu.abydos.arangodb" );
	private static ArangoDB arangoDB;
	private static ArangoDatabase arangoDatabase;
	private static final String databaseName = "Test";

	public static <T> T coalesce( @SuppressWarnings( "unchecked" ) T... objects ) {
		for ( T o:objects ) {
			if ( o != null ) {
				return o;
			}
		}
		return null;
	}

	/*public List<BaseDocument> getCars( ) {
		List<BaseDocument> cars = new ArrayList<>();
		try {
			Iterable<BaseDocument> documents = arangoDatabase.collection( "cars" ).insertDocuments( BaseDocument.class );
				
			documents.forEach( cars::add );
		} catch ( ArangoDBException e ) {
			e.printStackTrace();
		}
		return cars;
	}*/

	static {
		VelocyJack velocyJack = new VelocyJack();
		velocyJack.configure( ( mapper ) -> {
			mapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
			mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
			mapper.setDateFormat( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSX" ) );
			mapper.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
			mapper.configOverride( java.sql.Date.class )
				.setFormat( JsonFormat.Value.forPattern( "yyyy-MM-dd" )
					.withTimeZone( TimeZone.getTimeZone( (String) coalesce( System.getProperty( "sql.date.timezone" ), "Europe/Budapest" ) ) ) );
		} );
		String ipAddress = System.getProperty( "arango.server.ip", "localhost" );
		Integer port = Integer.parseInt( System.getProperty( "arango.server.port", "8529" ) );
		String userName = System.getProperty( "arango.db.user", "root" );
		String password = System.getProperty( "arango.db.password", "openSesame" );
		Integer maxConnection = Integer.parseInt( System.getProperty( "arango.db.max-connection", "200" ) );
		arangoDB = new ArangoDB.Builder().host( ipAddress, port )
			.user( userName )
			.password( password )
			.maxConnections( maxConnection )
			.registerModule( new VPackJdk8Module() )
			.registerModule( new VPackJodaModule() )
			.serializer( velocyJack )
			.build();
		arangoDatabase = arangoDB.db( databaseName );
		if ( !arangoDatabase.exists() ) {
			arangoDatabase = createDatabase( databaseName );
		} else {
			logger.info( "Database " + databaseName + " has already exists!" );
		}
	}

	public static ArangoDatabase getDataBase( ) {
		return arangoDatabase;
	}

	public static ArangoDatabase createDatabase( String dbName ) {
		ArangoDatabase ret = null;
		// create database
		try {
			arangoDB.createDatabase( dbName );
			ret = arangoDB.db( dbName );
			logger.info( "Database created: " + dbName );
		} catch ( ArangoDBException e ) {
			logger.severe( "Failed to create database: " + dbName + "\n" + ExceptionUtils.getStackTrace( e ) );
			throw e;
		}
		return ret;
	}

	public static ArangoCollection createCollection( String collectionName ) {
		// create collection
		ArangoCollection ret = null;
		try {
			CollectionEntity collection = arangoDatabase.createCollection( collectionName );
			ret = arangoDatabase.collection( collection.getName() );
			logger.info( "Collection created: " + collection.getName() );
		} catch ( ArangoDBException e ) {
			logger.severe( "Failed to create collection: " + collectionName + "\n" + ExceptionUtils.getStackTrace( e ) );
			throw e;
		}
		return ret;
	}

	public static DocumentCreateEntity<BaseDocument> createDocument( ArangoCollection collection ) {
		DocumentCreateEntity<BaseDocument> ret = null;
		// creating a document
		final BaseDocument myObject = new BaseDocument();
		myObject.setKey( UUID.randomUUID()
			.toString() );
		myObject.addAttribute( "a", "Foo" );
		myObject.addAttribute( "b", 42 );
		try {
			ret = collection.insertDocument( myObject );
			logger.info( "Document created" );
		} catch ( ArangoDBException e ) {
			logger.severe( "Failed to create document. \n" + ExceptionUtils.getStackTrace( e ) );
			throw e;
		}
		return ret;
	}

}
