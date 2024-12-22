package frontend.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;

import frontend.db.DBUtil;
import frontend.security.TokenService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path( "/auth" )
public class AuthResource {
	public ArangoDatabase db = DBUtil.getDataBase();
	public ArangoCollection accounts = db.collection( "accounts" );

	@POST
	@Path( "/login" )
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public Response login( UserLoginRequest loginRequest ) {
		try {
			// Query to find user with matching credentials
			String aqlQuery = "FOR acc IN @@collectionName " + "FILTER acc.username == @username AND acc.password == @password " + "RETURN acc";

			Map<String, Object> bindVars = new HashMap<>();
			bindVars.put( "@collectionName", accounts.name() );
			bindVars.put( "username", loginRequest.getUsername() );
			bindVars.put( "password", loginRequest.getPassword() );

			ArangoCursor<BaseDocument> cursor = db.query( aqlQuery, bindVars, null, BaseDocument.class );

			if ( cursor.hasNext() ) {
				// User found, retrieve the role from the database document
				BaseDocument userDoc = cursor.next();
				String role = userDoc.getAttribute( "role" )
					.toString(); // Get role from DB
				String token = TokenService.generateToken( loginRequest.getUsername(), role );

				// Return the token and role
				return Response.ok( new TokenResponse( token, role ) )
					.build();
			}

			return Response.status( Response.Status.UNAUTHORIZED )
				.entity( "Invalid credentials" )
				.build();
		} catch ( Exception e ) {
			return Response.status( Response.Status.INTERNAL_SERVER_ERROR )
				.entity( "Error during login: " + e.getMessage() )
				.build();
		}
	}

	private boolean isValidEmail( String email ) {
		// Regular expression for basic email validation
		String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
		Pattern pattern = Pattern.compile( emailRegex );
		return pattern.matcher( email )
			.matches();
	}

	@POST
	@Path( "/register" )
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public Response register( UserRegistrationRequest registrationRequest ) {
		try {
			if ( !isValidEmail( registrationRequest.getEmail() ) ) {
				return Response.status( Response.Status.BAD_REQUEST )
					.entity( "Invalid email format" )
					.build();
			}
			// Check if username or email already exists
			String checkQuery = "FOR acc IN @@collectionName " + "FILTER acc.username == @username OR acc.email == @email " + "RETURN acc";

			Map<String, Object> checkVars = new HashMap<>();
			checkVars.put( "@collectionName", accounts.name() );
			checkVars.put( "username", registrationRequest.getUsername() );
			checkVars.put( "email", registrationRequest.getEmail() );

			ArangoCursor<BaseDocument> cursor = db.query( checkQuery, checkVars, null, BaseDocument.class );

			if ( cursor.hasNext() ) {
				// Check if the conflict is with username or email
				return Response.status( Response.Status.CONFLICT )
					.entity( "Username or Email already exists" )
					.build();
			}

			// Create new user document
			BaseDocument userDoc = new BaseDocument();
			userDoc.addAttribute( "username", registrationRequest.getUsername() );
			userDoc.addAttribute( "password", registrationRequest.getPassword() );
			userDoc.addAttribute( "email", registrationRequest.getEmail() );
			userDoc.addAttribute( "role", registrationRequest.getRole() );

			// Insert into database
			accounts.insertDocument( userDoc );

			// Generate token for newly registered user
			String token = TokenService.generateToken( registrationRequest.getUsername(), registrationRequest.getRole() );
			String role = registrationRequest.getRole();

			return Response.status( Response.Status.CREATED )
				.entity( new TokenResponse( token, role ) )
				.build();

		} catch ( Exception e ) {
			return Response.status( Response.Status.INTERNAL_SERVER_ERROR )
				.entity( "Error during registration: " + e.getMessage() )
				.build();
		}
	}

	public static class UserLoginRequest {
		private String username;
		private String password;
		private String role;

		public String getUsername( ) {
			return username;
		}

		public void setUsername( String username ) {
			this.username = username;
		}

		public String getPassword( ) {
			return password;
		}

		public void setPassword( String password ) {
			this.password = password;
		}

		public String getRole( ) {
			return role;
		}

		public void setRole( String role ) {
			this.role = role;
		}
	}

	public static class TokenResponse {
		private String token;
		private String role;

		public TokenResponse( String token, String role ) {
			this.token = token;
			this.role = role;
		}

		public String getToken( ) {
			return token;
		}

		public String getRole( ) {
			return role;
		}
	}

	// Existing UserLoginRequest class...

	public static class UserRegistrationRequest {
		private String username;
		private String password;
		private String email;
		private String role;

		public String getUsername( ) {
			return username;
		}

		public void setUsername( String username ) {
			this.username = username;
		}

		public String getPassword( ) {
			return password;
		}

		public void setPassword( String password ) {
			this.password = password;
		}

		public String getEmail( ) {
			return email;
		}

		public void setEmail( String email ) {
			this.email = email;
		}

		public String getRole( ) {
			return role;
		}

		public void setRole( String role ) {
			this.role = role;
		}
	}

	// Existing TokenResponse class...
}
