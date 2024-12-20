package frontend.rest;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cars.Cars2;
import cars.ICars;
import frontend.rest.vo.User;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path( "/json" )
public class JsonResource {

	private static final ObjectMapper mapper = new ObjectMapper();

	@GET
	@Produces( MediaType.APPLICATION_JSON )
	public Response hello( ) {

		// create a JSON string
		ObjectNode json = mapper.createObjectNode();
		json.put( "result", "Jersey JSON example using Jackson 2.x" );
		return Response.status( Response.Status.OK )
			.entity( json )
			.build();
	}

	@Path( "/hello" )
	@GET
	@Produces( MediaType.APPLICATION_JSON )
	public Response hello2( ) {
		ICars cars2 = Cars2.getInstance();
		return Response.status( Response.Status.OK )
			.entity( cars2.getCars() )
			.build();
	}

	// Object to JSON
	@Path( "/{name}" )
	@GET
	@Produces( MediaType.APPLICATION_JSON )
	public User hello( @PathParam( "name" ) String name ) {
		return new User( 1, name );
	}

	// A list of objects to JSON
	@Path( "/all" )
	@GET
	@Produces( MediaType.APPLICATION_JSON )
	public List<User> helloList( ) {
		return Arrays.asList( new User( 1, "mkyong" ), new User( 2, "zilap" ) );
	}

	@Path( "/create" )
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public Response create( User user ) {

		return Response.status( Response.Status.CREATED )
			.entity( user )
			.build();

	}

}
