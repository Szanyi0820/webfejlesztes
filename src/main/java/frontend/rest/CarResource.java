package frontend.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.arangodb.model.AqlQueryOptions;
import com.fasterxml.jackson.databind.ObjectMapper;

import cars.Car;
import cars.Cars;
import cars.Owner;
import frontend.db.DBUtil;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path( "/car" )
public class CarResource {
	private static final ObjectMapper mapper = new ObjectMapper();
	private static List<Car> carList = new ArrayList<>();
	public ArangoDatabase db = DBUtil.getDataBase();
	public ArangoCollection cars = db.collection( "Cars" );
	public ArangoCollection owners = db.collection( "owners" );

	@GET
	@Produces( MediaType.APPLICATION_JSON )
	public Response getEmptyCar( ) throws Exception {
		Cars emptyCar = new Cars();
		return Response.status( Response.Status.OK )
			.entity( emptyCar )
			.build();
	}

	@Path( "/list" )
	@GET
	@Produces( MediaType.APPLICATION_JSON )
	public Response listCars( @QueryParam( "brandName" ) String brandName, @QueryParam( "color" ) String color ) {
		try {

			if ( !cars.exists() ) {
				cars = DBUtil.createCollection( "Cars" );
			}
			StringBuilder aqlQueryBuilder = new StringBuilder( "FOR r IN @@collectionName" );
			Map<String, Object> bindVars = new HashMap<>();
			bindVars.put( "@collectionName", cars.name() );
			if ( brandName != null ) {
				aqlQueryBuilder.append( " FILTER r.carType.brandName == @brandName" );
				bindVars.put( "brandName", brandName );
			}
			if ( color != null ) {
				if ( brandName != null ) {
					aqlQueryBuilder.append( " AND" );
				} else {
					aqlQueryBuilder.append( " FILTER" );
				}
				aqlQueryBuilder.append( " r.carType.color == @color" );
				bindVars.put( "color", color );
			}

			aqlQueryBuilder.append( " RETURN r" );
			String aqlQueryString = aqlQueryBuilder.toString();

			ArangoCursor<BaseDocument> retCursor = db.query( aqlQueryString, bindVars, new AqlQueryOptions(), BaseDocument.class );
			List<BaseDocument> result = retCursor.asListRemaining();

			return Response.status( Response.Status.OK )
				.entity( result )
				.build();

		} catch ( ArangoDBException e ) {
			return Response.status( Response.Status.INTERNAL_SERVER_ERROR )
				.entity( "Error fetching car list: " + e.getMessage() )
				.build();
		}
	}

	@Path( "/add" )
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public Response addCar( Car car ) {
		if ( car == null || car.getProperties() == null || car.getProperties()
			.getCarType() == null ) {
			return Response.status( Response.Status.BAD_REQUEST )
				.entity( "Missing required fields." )
				.build();
		}

		BaseDocument carDocument = new BaseDocument();
		carDocument.addAttribute( "carType", car.getProperties()
			.getCarType() );
		carDocument.addAttribute( "plateNumber", car.getProperties()
			.getPlateNumber() );
		carDocument.addAttribute( "factoryDate", car.getProperties()
			.getFactoryDate() );

		try {
			cars.insertDocument( carDocument );
		} catch ( ArangoDBException e ) {
			return Response.status( Response.Status.INTERNAL_SERVER_ERROR )
				.entity( "Error inserting car into database: " + e.getMessage() )
				.build();
		}

		return Response.status( Response.Status.CREATED )
			.entity( car )
			.build();
	}

	@Path( "/delete/{plateNumber}" )
	@DELETE
	@Produces( MediaType.APPLICATION_JSON )
	public Response deleteCar( @PathParam( "plateNumber" ) String plateNumber ) {
		String aqlQueryString = "FOR car IN @@collectionName FILTER car.plateNumber == @plateNumber RETURN car";
		Map bindVars = new HashMap<String, Object>();
		bindVars.put( "@collectionName", cars.name() );
		bindVars.put( "plateNumber", plateNumber );

		ArangoCursor<BaseDocument> cursor = db.query( aqlQueryString, bindVars, null, BaseDocument.class );

		BaseDocument carDoc = cursor.next();
		cars.deleteDocument( carDoc.getKey() );

		return Response.status( Response.Status.NO_CONTENT )
			.build();
	}

	@Path( "/update/{plateNumber}" )
	@PUT
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public Response updateCar( @PathParam( "plateNumber" ) String plateNumber, Car updatedCar ) {
		String aqlQueryString = "FOR car IN @@collectionName FILTER car.plateNumber == @plateNumber RETURN car";
		Map bindVars = new HashMap<String, Object>();
		bindVars.put( "@collectionName", cars.name() );
		bindVars.put( "plateNumber", plateNumber );

		ArangoCursor<BaseDocument> cursor = db.query( aqlQueryString, bindVars, null, BaseDocument.class );
		BaseDocument carDoc = cursor.next();
		carDoc.updateAttribute( "carType", updatedCar.getProperties()
			.getCarType() );
		carDoc.updateAttribute( "factoryDate", updatedCar.getProperties()
			.getFactoryDate() );
		carDoc.updateAttribute( "plateNumber", updatedCar.getProperties()
			.getPlateNumber() );
		cars.updateDocument( carDoc.getKey(), carDoc );

		return Response.status( Response.Status.OK )
			.header( "Access-Control-Allow-Origin", "http://localhost:4200" ) // Match frontend URL
			.header( "Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS" )
			.header( "Access-Control-Allow-Headers", "Content-Type, Authorization" )
			.header( "Access-Control-Allow-Credentials", "true" )
			.build();

	}

	@Path( "/owners/list" )
	@GET
	@Produces( MediaType.APPLICATION_JSON )
	public Response listOwners( ) {
		try {
			if ( !owners.exists() ) {
				return Response.status( Response.Status.NOT_FOUND )
					.entity( "Owners collection not found." )
					.build();
			}

			String aqlQueryString = "FOR owner IN @@collectionName RETURN owner";
			Map<String, Object> bindVars = new HashMap<>();
			bindVars.put( "@collectionName", owners.name() );

			ArangoCursor<BaseDocument> cursor = db.query( aqlQueryString, bindVars, new AqlQueryOptions(), BaseDocument.class );
			List<BaseDocument> result = cursor.asListRemaining();

			return Response.status( Response.Status.OK )
				.entity( result )
				.build();

		} catch ( ArangoDBException e ) {
			return Response.status( Response.Status.INTERNAL_SERVER_ERROR )
				.entity( "Error fetching owners list: " + e.getMessage() )
				.build();
		}
	}

	@Path( "/owners/add" )
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public Response addOwner( Owner owner ) {
		if ( owner == null || owner.getProperties() == null ) {
			return Response.status( Response.Status.BAD_REQUEST )
				.entity( "Missing required fields." )
				.build();
		}

		BaseDocument ownerDocument = new BaseDocument();
		ownerDocument.addAttribute( "name", owner.getProperties()
			.getName() );
		ownerDocument.addAttribute( "contactNumber", owner.getProperties()
			.getContactNumber() );
		ownerDocument.addAttribute( "email", owner.getProperties()
			.getEmail() );
		ownerDocument.addAttribute( "plateNumber", owner.getProperties()
			.getPlateNumber() );

		try {
			owners.insertDocument( ownerDocument );
		} catch ( ArangoDBException e ) {
			return Response.status( Response.Status.INTERNAL_SERVER_ERROR )
				.entity( "Error inserting owner into database: " + e.getMessage() )
				.build();
		}

		return Response.status( Response.Status.CREATED )
			.entity( owner )
			.build();
	}

	@Path( "/owners/delete/{plateNumber}" )
	@DELETE
	@Produces( MediaType.APPLICATION_JSON )
	public Response deleteOwner( @PathParam( "plateNumber" ) String plateNumber ) {
		String aqlQueryString = "FOR owner IN @@collectionName FILTER owner.plateNumber == @plateNumber RETURN owner";
		Map bindVars = new HashMap<String, Object>();
		bindVars.put( "@collectionName", owners.name() );
		bindVars.put( "plateNumber", plateNumber );

		ArangoCursor<BaseDocument> cursor = db.query( aqlQueryString, bindVars, null, BaseDocument.class );

		BaseDocument ownerDoc = cursor.next();
		owners.deleteDocument( ownerDoc.getKey() );

		return Response.status( Response.Status.NO_CONTENT )
			.build();

	}

	@Path( "/owners/update/{plateNumber}" )
	@PUT
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public Response updateOwner( @PathParam( "plateNumber" ) String plateNumber, Owner owner ) {
		// Log the incoming data for debugging
		System.out.println( "Received update request for plate number: " + plateNumber );
		System.out.println( "Owner properties received: " + owner.getProperties() );

		String aqlQueryString = "FOR owner IN @@collectionName FILTER owner.plateNumber == @plateNumber RETURN owner";
		Map bindVars = new HashMap<String, Object>();
		bindVars.put( "@collectionName", owners.name() );
		bindVars.put( "plateNumber", plateNumber );

		try {
			ArangoCursor<BaseDocument> cursor = db.query( aqlQueryString, bindVars, null, BaseDocument.class );

			// Check if the owner exists
			/*if ( !cursor.hasNext() ) {
				return Response.status( Response.Status.NOT_FOUND )
					.entity( "Owner with plate number " + plateNumber + " not found." )
					.build();
			}*/

			// Update the owner's document fields
			BaseDocument ownerDoc = cursor.next();
			ownerDoc.updateAttribute( "plateNumber", owner.getProperties()
				.getPlateNumber() ); // Use path parameter
			ownerDoc.updateAttribute( "name", owner.getProperties()
				.getName() );
			ownerDoc.updateAttribute( "contactNumber", owner.getProperties()
				.getContactNumber() );
			ownerDoc.updateAttribute( "email", owner.getProperties()
				.getEmail() );

			// Print the document before and after updating to verify changes
			System.out.println( "Before update: " + ownerDoc );
			owners.updateDocument( ownerDoc.getKey(), ownerDoc );
			System.out.println( "After update: " + ownerDoc );

			// Return the updated document with headers for frontend integration
			return Response.status( Response.Status.OK )
				.header( "Access-Control-Allow-Origin", "http://localhost:4200" )
				.header( "Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS" )
				.header( "Access-Control-Allow-Headers", "Content-Type, Authorization" )
				.header( "Access-Control-Allow-Credentials", "true" )
				.entity( ownerDoc )
				.build();
		} catch ( ArangoDBException e ) {
			// Log the exception and return an error response
			System.err.println( "Error updating owner: " + e.getMessage() );
			return Response.status( Response.Status.INTERNAL_SERVER_ERROR )
				.entity( "Error updating owner: " + e.getMessage() )
				.build();
		}
	}

}